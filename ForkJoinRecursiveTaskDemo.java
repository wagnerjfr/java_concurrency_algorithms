package main.concurrency;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * Fork/Join Framework
 * Framework for executing recursive, divide-and-conquer work with multiple processors
 * Sample of recursive sum of a range of numbers
 */
public class ForkJoinRecursiveTaskDemo {

    static class RecursiveSum extends RecursiveTask<Long> {

        long start, end;

        RecursiveSum(long start, long end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            if (end - start <= 100_000) {
                long total = 0;
                for (long i = start; i <= end; i++) {
                    total += i;
                }
                return total;
            } else {
                long mid = (start + end) / 2;
                RecursiveSum left = new RecursiveSum(start, mid);
                RecursiveSum right = new RecursiveSum(mid + 1, end);
                left.fork();
                return right.compute() + left.join();
            }
        }
    }

    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool();
        long total = pool.invoke(new RecursiveSum(0, 1_000_000_000));
        System.out.println(total);
    }
}
