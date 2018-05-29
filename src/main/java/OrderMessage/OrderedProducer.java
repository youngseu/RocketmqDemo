package OrderMessage;

import com.alibaba.rocketmq.client.producer.DefaultMQProducer;
import com.alibaba.rocketmq.client.producer.MQProducer;
import com.alibaba.rocketmq.client.producer.MessageQueueSelector;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.common.message.Message;
import com.alibaba.rocketmq.common.message.MessageQueue;
import com.alibaba.rocketmq.remoting.common.RemotingHelper;

import java.util.List;

/**
 * Created by yang on 18-5-28.
 */
public class OrderedProducer {
    public static void main(String[] args) throws Exception {
        //Instantiate with a producer group name.
        DefaultMQProducer producer = new DefaultMQProducer("i2p_producer");
        //set nameserver
        producer.setNamesrvAddr("localhost:9876");
        //Launch the instance.
        producer.start();

        String[] tags = new String[] {"TagA", "TagB"};
        for (int i = 0; i < 10000; i++) {
            int orderId = i % 16;
            //Create a message instance, specifying topic, tag and message body.
            Message msg = new Message("i2p", tags[i % tags.length], "KEY" + i,
                    ("Hello RocketMQ " + i).getBytes());

            SendResult sendResult = producer.send(msg, new MessageQueueSelector() {
                //select msg queue
                @Override
                public MessageQueue select(List<MessageQueue> mqs, Message msg, Object arg) {

                    //arg is orderId
                    Integer id = (Integer) arg;

                    //mqs is queue set
                    int index = id % mqs.size();

                    //return mq
                    return mqs.get(index);
                }
            }, orderId);

            System.out.printf("%s%n", sendResult);
        }
        //server shutdown
        producer.shutdown();
    }
}