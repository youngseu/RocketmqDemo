import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueTest {
    public static void main(String[] args) {
        ConcurrentLinkedQueue concurrentLinkedQueue = new ConcurrentLinkedQueue();
        ConcurrentHashMap<Integer, ConcurrentLinkedQueue> map = new ConcurrentHashMap<Integer, ConcurrentLinkedQueue>();

        concurrentLinkedQueue.add(1);
        concurrentLinkedQueue.add(2);
        System.out.println("size:"+concurrentLinkedQueue.size());

        concurrentLinkedQueue.poll();
        concurrentLinkedQueue.poll();
        System.out.println("size:"+concurrentLinkedQueue.size());

        if (!map.containsKey(1)) {
            map.put(1, concurrentLinkedQueue);
        }
        System.out.println("size:"+map.get(1).size());

        map.get(1).add(3);
        map.get(1).add(4);
        System.out.println("size:"+map.get(1).size());

        System.out.println("iterable");
        Iterator iterable = map.get(1).iterator();
        while (iterable.hasNext()) {
            System.out.println((Integer) iterable.next());
        }
    }
}
