package I2P;

import Config.CONFIG;
import com.alibaba.rocketmq.client.consumer.DefaultMQPushConsumer;
import com.alibaba.rocketmq.common.consumer.ConsumeFromWhere;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//consumer i2p msg
public class I2PConsumer {
    public static void main(String[] args) throws Exception {
        //consumer
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("i2p_consumer");

        //threadpool
        ExecutorService threadpool = Executors.newCachedThreadPool();

        //all stream
        ConcurrentHashMap<Integer, I2PStream> map = new ConcurrentHashMap<Integer, I2PStream>();

        consumer.setNamesrvAddr(CONFIG.NAMESERVER);

        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);

        //consumer.subscribe("i2p", "TagA || TagB");
        consumer.subscribe("i2p", "TagA");

        consumer.registerMessageListener(new I2PListenerOrderly(threadpool,map));

        consumer.start();

        System.out.printf("Consumer Started.%n");
    }
}
