import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Starts 10 Callable tasks in parallel, which one will wait from 1s to 5s and increment an AtomicInteger.
 * Wait for all 10 threads using CountDownLatch[1] and print the result at the same time in the end.
 * [1] Releases when a count value reaches zero
 */
public class CountDownLatchDemo2 {
    private static final int NUM_TASKS = 10;
    private static final AtomicInteger counter = new AtomicInteger(0);

    private static class MyTask implements Callable<String> {
        private final CountDownLatch latch;

        MyTask(CountDownLatch latch) {
            this.latch = latch;
        }

        public String call() throws Exception {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 5000));
                counter.getAndIncrement();
            } finally {
                latch.countDown();
            }
            return String.format("%s | counter %d", Instant.now(), counter.get());
        }
    }

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(NUM_TASKS);

        List<MyTask> tasks = new ArrayList<>();
        for (int i = 0; i < NUM_TASKS; i++) {
            tasks.add(new CountDownLatchDemo2.MyTask(latch));
        }

        ExecutorService pool = Executors.newFixedThreadPool(NUM_TASKS);
        try {
            List<Future<String>> results = pool.invokeAll(tasks);

            if (!latch.await(6, TimeUnit.SECONDS)) {
                System.err.println("Time out!");
            }

            for (Future<String> result : results) {
                System.out.println(result.get());
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
}
