import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Fork/Join Framework
 * Framework for executing recursive, divide-and-conquer work with multiple processors
 * Replace the values of a ArrayList from 0 to 1, or from 1 to 0
 */
public class ForkJoinRecursiveActionDemo {

    private static final List<Integer> list = new ArrayList<>();

    private static class RecursiveReplace extends RecursiveAction {
        private final int start;
        private final int end;

        private RecursiveReplace() {
            this.start = 0;
            this.end = list.size();
        }

        private RecursiveReplace(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {
            if (end - start <= 100) {
                for (int i = start; i < end; i++) {
                    list.set(i, list.get(i) == 0 ? 1 : 0);
                }
            } else {
                int mid = (start + end) / 2;
                RecursiveReplace left = new RecursiveReplace(start, mid);
                RecursiveReplace right = new RecursiveReplace(mid + 1, end);
                invokeAll(left, right);
            }
        }
    }

    public static void main(String[] args) {
        ForkJoinPool pool = new ForkJoinPool();
        for (int i = 0; i < 100_000; i++) {
            list.add(ThreadLocalRandom.current().nextInt(0, 2));
        }
        System.out.println(list);
        pool.invoke(new RecursiveReplace());
        System.out.println(list);
    }
}
