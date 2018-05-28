import NormalMessage.RocketMQConsumer;
import NormalMessage.RocketMQListener;

/**
 * Created by yang on 18-5-16.
 */
public class RocketMQConsumerTest {


    public static void main(String[] args) {


        String mqNameServer = "localhost:9876";
        String mqTopics = "1";

        String consumerMqGroupName = "1";
        RocketMQListener mqListener = new RocketMQListener();
        RocketMQConsumer mqConsumer = new RocketMQConsumer(mqListener, mqNameServer, consumerMqGroupName, mqTopics);
        mqConsumer.init();


        try {
            Thread.sleep(1000 * 60L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}