package I2P;

import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyContext;
import com.alibaba.rocketmq.client.consumer.listener.ConsumeOrderlyStatus;
import com.alibaba.rocketmq.client.consumer.listener.MessageListenerOrderly;
import com.alibaba.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

public class I2PListenerOrderly implements MessageListenerOrderly {
    //thread pool
    private ExecutorService executorService;

    //streams
    private ConcurrentHashMap<Integer,I2PStream> map;

    //i2p classifier
    private I2PClassifier i2PClassifier;

    public I2PListenerOrderly(ExecutorService executorService, ConcurrentHashMap<Integer, I2PStream> map) {
        this.executorService = executorService;
        this.map = map;
        this.i2PClassifier = new I2PClassifier();
    }

    @Override
    public ConsumeOrderlyStatus consumeMessage(List<MessageExt> msgs, ConsumeOrderlyContext context) {
        context.setAutoCommit(true);
        for (MessageExt msg : msgs) {
            //apply task
            executorService.execute(new I2PPackageHandler(map,i2PClassifier,msg.getBody()));
        }
        return ConsumeOrderlyStatus.SUCCESS;
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public ConcurrentHashMap<Integer, I2PStream> getMap() {
        return map;
    }

    public void setMap(ConcurrentHashMap<Integer, I2PStream> map) {
        this.map = map;
    }

    public I2PClassifier getI2PClassifier() {
        return i2PClassifier;
    }

    public void setI2PClassifier(I2PClassifier i2PClassifier) {
        this.i2PClassifier = i2PClassifier;
    }
}
