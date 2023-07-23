/**
 * In this example, we have two threads, threadA and threadB, that share a common object lock. threadA enters a synchronized
 * block, does some work, then calls lock.wait() to wait for threadB to notify it. threadB, in its synchronized block, does some work,
 * and then calls lock.notify() to notify threadA to resume.
 * When you run this code, you'll observe that threadA will wait until threadB calls lock.notify(), demonstrating how synchronization
 * using synchronized, wait, and notify ensures proper coordination between the two threads.
 */
public class SynchronizedDemo {
    public static void main(String[] args) {
        Object lock = new Object();

        // Thread A
        Thread threadA = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("Thread A is doing some work.");
                    Thread.sleep(2000);
                    System.out.println("Thread A is waiting for Thread B to notify.");
                    lock.wait();
                    System.out.println("Thread A is resuming its work.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Thread B
        Thread threadB = new Thread(() -> {
            synchronized (lock) {
                try {
                    System.out.println("Thread B is doing some work.");
                    Thread.sleep(3000);
                    System.out.println("Thread B is notifying Thread A to resume.");
                    lock.notify();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        // Start both threads
        threadA.start();
        threadB.start();

        // Wait for both threads to finish
        try {
            threadA.join();
            threadB.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("Both threads have completed.");
    }
}
