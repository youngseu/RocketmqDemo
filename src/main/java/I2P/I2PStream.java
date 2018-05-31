package I2P;

import Config.CONFIG;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

//save an I2P stream
public class I2PStream {
    //compare to judge_window_size
    private AtomicInteger update_time;

    //classifier
    private I2PClassifier i2pClassifier;

    //stream
    ConcurrentLinkedQueue<I2PPackage> concurrentLinkedQueue;

    public I2PStream(I2PClassifier i2pClassifier) {
        update_time.set(0);
        this.concurrentLinkedQueue = new ConcurrentLinkedQueue<I2PPackage>();
        this.i2pClassifier = i2pClassifier;
    }

    public AtomicInteger getUpdate_time() {
        return update_time;
    }

    public ConcurrentLinkedQueue<I2PPackage> getConcurrentLinkedQueue() {
        return concurrentLinkedQueue;
    }

    public void setConcurrentLinkedQueue(ConcurrentLinkedQueue<I2PPackage> concurrentLinkedQueue) {
        this.concurrentLinkedQueue = concurrentLinkedQueue;
    }

    //handle package add and classifier
    public void addAndIncrease(I2PPackage i2pPackage) {
        concurrentLinkedQueue.add(i2pPackage);
        if (concurrentLinkedQueue.size() >= CONFIG.SLIDING_WINDOW_SIZE) {
            concurrentLinkedQueue.poll();
            //triger classifier
            if (update_time.addAndGet(1) >= CONFIG.JUDGE_WINDOW_SIZE) {
                //renew
                update_time.set(0);
                if (i2pClassifier.classifier(concurrentLinkedQueue)) {
                    System.out.println(i2pPackage);
                }
            }
        }
    }

    public I2PClassifier getI2pClassifier() {
        return i2pClassifier;
    }

    public void setI2pClassifier(I2PClassifier i2pClassifier) {
        this.i2pClassifier = i2pClassifier;
    }
}
