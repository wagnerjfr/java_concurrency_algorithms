import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Sample of using ReentrantReadWriteLock
 * A box with some items and 5 worker threads that can randomly add, remove, peek or print the items
 * Each worker has 5 of those above operations that is chosen also randomly
 */
public class ReentrantReadWriteLockDemo {

    private static final Integer[] array = {1,2,3,4,5};
    private static final Box box = new Box(array);

    private static class Box {
        private final List<Integer> items = new ArrayList<>();
        private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
        private final Lock r = rwl.readLock();
        private final Lock w = rwl.writeLock();

        private Box(Integer[] array) {
            Collections.addAll(items, array);
        }

        int peek() {
            r.lock();
            try { return items.get(0); }
            finally { r.unlock(); }
        }

        String print() {
            r.lock();
            try {
                StringBuilder sb = new StringBuilder("|");
                items.forEach(c -> sb.append(c).append("|"));
                return sb.toString();
            }
            finally { r.unlock(); }
        }

        int remove() {
            w.lock();
            try {
                if (!items.isEmpty()) {
                    return items.remove(0);
                }
                return 0;
            }
            finally { w.unlock(); }
        }

        void add(int i) {
            w.lock();
            try { items.add(i); }
            finally { w.unlock(); }
        }
    }

    private static class Worker implements Runnable {
        private static final int MAX_OP = 5;
        private final String name;
        private boolean stop = false;
        private int count = 1;

        private Worker(String name) {
            this.name = name;
        }

        @Override
        public void run() {
            while (!stop) {
                String info = String.format("%s [%d/%d]", this.name, count, MAX_OP);
                int r = ThreadLocalRandom.current().nextInt(0,5);
                switch (r) {
                    case 1:
                        int n = ThreadLocalRandom.current().nextInt(6,10);
                        box.add(n);
                        System.out.println(info + " added item " + n);
                        break;
                    case 2:
                        int re = box.remove();
                        System.out.println(info + " removed item " + re);
                        break;
                    case 3:
                        int pe = box.peek();
                        System.out.println(info + " peeked item " + pe);
                        break;
                    default:
                        String s = box.print();
                        System.out.println(info + " printed list " + s);
                        break;
                }
                try {
                    Thread.sleep(ThreadLocalRandom.current().nextInt(500,2000));
                    if (count == MAX_OP) {
                        this.stop();
                    }
                    count++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        private void stop() {
            this.stop = true;
        }
    }

    public static void main(String[] args) {
        Thread t;
        for (int i = 1; i <= 5; i++) {
            t = new Thread(new Worker("Worker" + i));
            t.start();
        }
    }
}
