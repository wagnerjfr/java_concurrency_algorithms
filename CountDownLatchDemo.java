package main.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Order 10 threads to first operate the additions and then multiplications on a shared variable using CountDownLatch[1]
 * [1] Releases when a count value reaches zero
 */
public class CountDownLatchDemo {

    private static class Counter extends Thread {

        public static int MAX_NUM_THREADS = 10;
        public static int sum = 0;
        private static Lock lock = new ReentrantLock();
        private static CountDownLatch barrier = new CountDownLatch(MAX_NUM_THREADS/2);

        private String name;

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
                barrier.countDown();
            } else { //Multiplier
                try {
                    barrier.await();
                } catch (InterruptedException e) { e.printStackTrace(); }
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

    public static void main(String args[]) throws InterruptedException {
        List<Counter> list = new ArrayList<>();
        for (int i = 0; i < Counter.MAX_NUM_THREADS/2 ; i++) {
            list.add(new Counter("Adder-" + i));
            list.add(new Counter("Multiplier-" + i));
        }
        for (Counter c : list) {
            c.start();
        }
        for (Counter c : list) {
            c.join();
        }
        System.out.println("Final value and is " + Counter.sum);
    }
}
