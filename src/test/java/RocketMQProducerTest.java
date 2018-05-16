import com.alibaba.rocketmq.common.message.Message;

/**
 * Created by yang on 18-5-16.
 */
public class RocketMQProducerTest {

    public static void main(String[] args) {

        String mqNameServer = "localhost:9876";
        String mqTopics = "MQ-MSG-TOPICS-TEST";

        String producerMqGroupName = "PRODUCER-MQ-GROUP";
        RocketMQProducer mqProducer = new RocketMQProducer(mqNameServer, producerMqGroupName, mqTopics);
        mqProducer.init();


        for (int i = 0; i < 5; i++) {

            Message message = new Message();
            message.setBody(("I send message to RocketMQ " + i).getBytes());
            mqProducer.send(message);
        }


    }

}