package main.concurrency;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Alternate 3 threads while consuming of a share resource using Condition variable
 */

class Consumer extends Thread {

    private static int items = 10;
    private static Lock lock = new ReentrantLock();
    private static Condition alternate = lock.newCondition();

    private int id;

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

public class ConditionVariableDemo {
    public static void main(String args[]) {
        for (int i = 0; i <= 2; i++) {
            new Consumer(i).start();
        }
    }
}
