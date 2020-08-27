package thread;

import java.util.concurrent.ThreadLocalRandom;

/**
 * In this example, 3 threads will print the 3 consecutive exponential values of 1, 2 and 3.
 * All of them will be using the SharedUtil class for it.
 * P.S. SharedUtil uses Java ThreadLocal class which enables us to create variables that can only be read and written by the same thread.
 */
public class ThreadLocalDemo {

    public static void main(String[] args) {

        new ThreadLocalDemo().execute();

    }

    private void execute() {
        for (int i = 1; i <= 3; i++) {
            new Thread(new Task(i), "Thread exp" + i).start();
        }
    }

    private class Task implements Runnable {

        private int num;

        Task(int num) {
            this.num = num;
        }

        @Override
        public void run() {
            try {
                SharedUtil.setCounter(num);

                for (int i = 0; i < 3; i++) {
                    SharedUtil.calculateAndPrint();
                    Thread.sleep(ThreadLocalRandom.current().nextInt(100, 500));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                SharedUtil.remove();
            }
        }
    }

    private static class SharedUtil {
        private static ThreadLocal<Integer> threadLocalCounter = ThreadLocal.withInitial(() -> 0);
        private static ThreadLocal<Integer> threadLocalAccumulator = ThreadLocal.withInitial(() -> 0);

        static void setCounter(int number) {
            threadLocalCounter.set(number);
            threadLocalAccumulator.set(number);
        }

        static void calculateAndPrint() {
            System.out.println(Thread.currentThread().getName() + ": " + threadLocalAccumulator.get());
            threadLocalAccumulator.set(threadLocalAccumulator.get() * threadLocalCounter.get());
        }

        static void remove() {
            threadLocalAccumulator.remove();
            threadLocalCounter.remove();
        }
    }
}
