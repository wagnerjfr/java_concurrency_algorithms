import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Alternate 3 threads while consuming of a share resource using Condition variable
 */

public class ConditionVariableDemo {
    private static int items = 10;
    private static final Lock lock = new ReentrantLock();
    private static final Condition alternate = lock.newCondition();

    static class Consumer extends Thread {
        private final int id;

        Consumer(int id) {
            this.id = id;
        }

        public void run() {
            while (items > 0) {
                lock.lock();
                try {
                    while (items % 3 != id && items > 0) {
                        alternate.await();
                    }
                    if (items > 0) {
                        System.out.println("Consumer " + id + " took the item " + items);
                        items--;
                        alternate.signalAll();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public static void main(String[] args) {
        for (int i = 0; i <= 2; i++) {
            new Consumer(i).start();
        }
    }
}
