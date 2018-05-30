package I2P;

import Config.CONFIG;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

//save an I2P stream
public class I2PStream {
    //compare to judge_window_size
    private AtomicInteger update_time;

    //stream
    ConcurrentLinkedQueue<I2PPackage> concurrentLinkedQueue;

    public I2PStream() {
        update_time.set(0);
        this.concurrentLinkedQueue = new ConcurrentLinkedQueue<I2PPackage>();
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

    public int addAndIncrease(I2PPackage i2pPackage) {
        concurrentLinkedQueue.add(i2pPackage);
        if (concurrentLinkedQueue.size() >= CONFIG.SLIDING_WINDOW_SIZE) {
            concurrentLinkedQueue.poll();
        }
        return update_time.addAndGet(1);
    }
}
