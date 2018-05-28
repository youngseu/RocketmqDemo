package Jnetpcap;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapHeader;
import org.jnetpcap.nio.JBuffer;
import org.jnetpcap.nio.JMemory;
import org.jnetpcap.packet.JRegistry;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.format.FormatUtils;
import org.jnetpcap.protocol.lan.Ethernet;
import org.jnetpcap.protocol.network.Ip4;
import org.jnetpcap.protocol.tcpip.Tcp;
import org.jnetpcap.protocol.tcpip.Udp;

/**
 * Created by yang on 18-5-28.
 */
public class JnetpcapDemo {
    public static void main(String[] args) {
        final String FILE_NAME = "/home/yang/eclipse_workspace/src/demo/test.pcap";
        StringBuilder errbuf = new StringBuilder(); // For any error msgs

        // open up the selected device
        Pcap pcap = Pcap.openOffline(FILE_NAME, errbuf);

        if (pcap == null) {
            System.err.printf("Error while opening file for capture: " + errbuf.toString());
            return;
        }

        /***************************************************************************
         * Second - we create our main loop and our application We create some
         * objects we will be using and reusing inside the loop
         **************************************************************************/
        Ethernet eth = new Ethernet();
        Ip4 ip = new Ip4();
        Tcp tcp = new Tcp();
        Udp udp = new Udp();
        PcapHeader hdr = new PcapHeader(JMemory.POINTER);
        JBuffer buf = new JBuffer(JMemory.POINTER);

        /***************************************************************************
         * Third - we must map pcap's data-link-type to jNetPcap's protocol IDs.
         * This is needed by the scanner so that it knows what the first header
         * in the packet is.
         **************************************************************************/
        int id = JRegistry.mapDLTToId(pcap.datalink());

        /***************************************************************************
         * Fourth - we peer header and buffer (not copy, think of C pointers)
         **************************************************************************/
        while (pcap.nextEx(hdr, buf) == Pcap.NEXT_EX_OK) {

            /*************************************************************************
             * Fifth - we copy header and buffer data to new packet object
             ************************************************************************/
            PcapPacket packet = new PcapPacket(hdr, buf);

            /*************************************************************************
             * Six- we scan the new packet to discover what headers it contains
             ************************************************************************/
            packet.scan(id);

			/*
			 * We use FormatUtils (found in org.jnetpcap.packet.format package),
			 * to convert our raw addresses to a human readable string.
			 */
            // if (packet.hasHeader(eth)) {
            // String str = FormatUtils.mac(eth.source());
            // System.out.printf("#%d: eth.src=%s\n", packet.getFrameNumber(),
            // str);
            // }
            if (packet.hasHeader(ip)) {
                String srcip = FormatUtils.ip(ip.source());
                String dstip = FormatUtils.ip(ip.destination());
                // int srcport =
                System.out.printf("#%d: ip.src= %s\n", packet.getFrameNumber(), srcip);
                System.out.printf("               %s\n", dstip);
            }
            if (packet.hasHeader(tcp)) {
                String srcport = String.valueOf(tcp.source());
                String dstport = String.valueOf(tcp.destination());

                System.out.printf("srcport:%s  \n", srcport);
                System.out.printf("dstport:%s  \n", dstport);
                System.out.println("length: " + tcp.getPayloadLength());

            }
            if (packet.hasHeader(udp)) {
                String srcport = String.valueOf(udp.source());
                String dstport = String.valueOf(udp.destination());
                System.out.printf("srcport:%s  \n", srcport);
                System.out.printf("dstport:%s  \n", dstport);
                System.out.println("length                             : " + udp.getPayloadLength());
            }
        }

        /*************************************************************************
         * Last thing to do is close the pcap handle
         ************************************************************************/
        pcap.close();
    }
}
