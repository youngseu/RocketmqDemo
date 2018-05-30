package Jnetpcap;

/**
 * Created by yang on 18-5-28.
 */

import Config.CONFIG;
import com.alibaba.rocketmq.client.exception.MQBrokerException;
import com.alibaba.rocketmq.client.exception.MQClientException;
import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.remoting.exception.RemotingException;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;

import java.util.ArrayList;
import java.util.List;

public class DumpPcap {
    /**
     * Main startup method
     *
     * @param args ignored
     */
    public static void main(final String[] args) throws Exception {

        List<PcapIf> alldevs = new ArrayList<PcapIf>(); // Will be filled with NICs
        StringBuilder errbuf = new StringBuilder(); // For any error msgs

        System.out.println("Options:args[0] is dev choose");
        /***************************************************************************
         * First get a list of devices on this system
         **************************************************************************/
        int r = Pcap.findAllDevs(alldevs, errbuf);
        if (r == Pcap.NOT_OK || alldevs.isEmpty()) {
            System.err.printf("Can't read list of devices, error is %s", errbuf
                    .toString());
            return;
        }

        System.out.println("Network devices found:");

        int i = 0;
        for (PcapIf device : alldevs) {
            String description =
                    (device.getDescription() != null) ? device.getDescription()
                            : "No description available";
            System.out.printf("#%d: %s [%s]\n", i++, device.getName(), description);
        }

        PcapIf device = alldevs.get(Integer.parseInt(args[0])); // We know we have atleast 1 device
        System.out
                .printf("\nChoosing '%s' on your behalf:\n",
                        (device.getDescription() != null) ? device.getDescription()
                                : device.getName());

        /***************************************************************************
         * Second we open up the selected device
         **************************************************************************/
        int snaplen = 64 * 1024;           // Capture all packets, no trucation
        int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
        int timeout = 10 * 1000;           // 10 seconds in millis
        final Pcap pcap =
                Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);

        if (pcap == null) {
            System.err.printf("Error while opening device for capture: "
                    + errbuf.toString());
            return;
        }

        /***************************************************************************
         * Add rocketmq producer msg
         **************************************************************************/
        DefaultMQProducer producer = new DefaultMQProducer(CONFIG.PRODUCER_GROUP);

        producer.setNamesrvAddr(CONFIG.NAMESERVER);

        producer.start();

        /***************************************************************************
         * Third we create a packet handler which will receive packets from the
         * libpcap loop.
         **************************************************************************/
        PcapPacketHandler<DefaultMQProducer> jpacketHandler = new PcapPacketHandler<DefaultMQProducer>() {

            Ip4 ip = new Ip4();
            Tcp tcp = new Tcp();
            String[] tags = new String[]{"TagA", "TagB"};

            public void nextPacket(PcapPacket packet, DefaultMQProducer producer) {
                String srcip = null, dstip = null, srcport = null, dstport = null;
                if (packet.hasHeader(ip)) {
                    srcip = FormatUtils.ip(ip.source());
                    dstip = FormatUtils.ip(ip.destination());
                }
                if (packet.hasHeader(tcp) && srcip != null && dstip != null) {
                    System.out.println("tcp");
                    srcport = String.valueOf(tcp.source());
                    dstport = String.valueOf(tcp.destination());

                    StringBuilder stringBuilder = new StringBuilder();

                    System.out.printf("#%d: ", packet.getFrameNumber());

                    int pcap_id = Math.abs(srcip.hashCode() + srcport.hashCode() + dstip.hashCode() + dstport.hashCode());

                    //msg body
                    stringBuilder.append(srcip).append(",").append(tcp.source()).append(",")
                            .append(dstip).append(",").append(tcp.destination()).append(",")
                            .append(tcp.getPayloadLength()).append(",").append(pcap_id);

                    //msg
                    Message msg = new Message(CONFIG.PRODUCER_TOPIC, tags[pcap_id % tags.length], "KEY" + pcap_id,
                            stringBuilder.toString().getBytes());

                    try {
                        SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                            @Override
                            public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {
                                //arg is pcap_id
                                Integer id = (Integer) arg;

                                //mqs is queue set
                                int index = id % mqs.size();

                                //return mq
                                return mqs.get(index);
                            }
                        }, pcap_id);
                        System.out.println(sendResult);
                    } catch (MQClientException e) {
                        e.printStackTrace();
                    } catch (RemotingException e) {
                        e.printStackTrace();
                    } catch (MQBrokerException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println(stringBuilder.toString());
                    System.out.println();
                }
            }
        };


        /***************************************************************************
         * Fourth we enter the loop and tell it to capture 10 packets. The loop
         * method does a mapping of pcap.datalink() DLT value to JProtocol ID, which
         * is needed by JScanner. The scanner scans the packet buffer and decodes
         * the headers. The mapping is done automatically, although a variation on
         * the loop method exists that allows the programmer to sepecify exactly
         * which protocol ID to use as the data link type for this pcap interface.
         **************************************************************************/
        pcap.loop(-1, jpacketHandler, producer);

        /***************************************************************************
         * Last thing to do is close the pcap handle
         **************************************************************************/
        pcap.close();
    }
}