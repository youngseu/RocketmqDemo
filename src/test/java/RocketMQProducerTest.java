import NormalMessage.RocketMQProducer;
import com.alibaba.rocketmq.common.message.Message;

/**
 * Created by yang on 18-5-16.
 */
public class RocketMQProducerTest {

    public static void main(String[] args) {

        String mqNameServer = "localhost:9876";
        String mqTopics = "1";

        String producerMqGroupName = "1";
        RocketMQProducer mqProducer = new RocketMQProducer(mqNameServer, producerMqGroupName, mqTopics);
        mqProducer.init();


        for (int i = 0; i < 5; i++) {

            Message message = new Message();
            message.setBody(("I send message to RocketMQ " + i).getBytes());
            mqProducer.send(message);
        }

        mqProducer.shutdown();
    }

}