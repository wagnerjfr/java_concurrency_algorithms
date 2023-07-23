import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Order 10 threads to first operate the additions and then multiplications on a shared variable using CyclicBarrier[1]
 * [1] Releases when a number of threads are waiting
 */
public class CyclicBarrierDemo {
    public static int MAX_NUM_THREADS = 10;
    public static int sum = 0;
    private static final Lock lock = new ReentrantLock();
    private static final CyclicBarrier barrier = new CyclicBarrier(MAX_NUM_THREADS);

    private static class Counter extends Thread {
        private final String name;

        Counter(String name) {
            this.name = name;
        }

        public void run() {
            if (name.contains("Adder")) {
                lock.lock();
                try {
                    sum += 5;
                    System.out.println("Counter " + name + " changed the value and now is " + sum);
                } finally {
                    lock.unlock();
                }
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) { e.printStackTrace(); }
            } else { //Multiplier
                try {
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) { e.printStackTrace(); }
                lock.lock();
                try {
                    sum *= 2;
                    System.out.println("Counter " + name + " changed the value and now is " + sum);
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        List<Counter> list = new ArrayList<>();
        for (int i = 0; i < MAX_NUM_THREADS / 2 ; i++) {
            list.add(new Counter("Adder-" + i));
            list.add(new Counter("Multiplier-" + i));
        }
        for (Counter c : list) {
            c.start();
        }
        for (Counter c : list) {
            c.join();
        }
        System.out.println("Final value and is " + sum);
    }
}
