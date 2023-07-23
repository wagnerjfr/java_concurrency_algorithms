import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Simple sample of Producer/Consumer using ArrayBlockingQueue (ThreadSafe)
 * - One Producer adds items in a pipeline in a frequency of 200ms
 * - The pipeline has a limit of 5 items
 * - Two consumers take items from the pipeline and consumes it in 500ms
 */
public class BlockingQueueDemo {

    private static final String STOP = "producer stopped";
    private static final int CONSUMERS = 2;
    private static final int CAPACITY = 5;

    private static class Producer extends Thread {
        private final BlockingQueue<String> pipeline;

        Producer(BlockingQueue<String> pipeline) {
            this. pipeline = pipeline;
        }

        public void run() {
            int nItems = 1;
            while (nItems <= 20) {
                try {
                    String item = "item" + nItems;
                    pipeline.offer(item);
                    String capacity = String.format(" [%d/%d]", pipeline.size(), CAPACITY);
                    System.out.println("Producer is adding " + item + capacity );
                    nItems++;
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Wait for the items to be consume
            while (pipeline.remainingCapacity() != CAPACITY) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // Send command to stop consumers
            for (int i = 0; i < CONSUMERS; i++) {
                pipeline.add(STOP);
            }
        }
    }

    private static class Consumer extends Thread {
        private final BlockingQueue<String> pipeline;
        private final String name;

        Consumer(String name, BlockingQueue<String> pipeline) {
            this.name = name;
            this. pipeline = pipeline;
        }

        public void run() {
            while (true) {
                try {
                    String item = pipeline.take();
                    if (item.equals(STOP)) {
                        break;
                    }
                    System.out.println(name + " took " + item);
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> pipeline = new ArrayBlockingQueue<>(CAPACITY);
        new Producer(pipeline).start();
        Thread.sleep(100);
        for (int i = 0; i < CONSUMERS; i++) {
            new Consumer("Consumer" + i, pipeline).start();
        }
    }
}
