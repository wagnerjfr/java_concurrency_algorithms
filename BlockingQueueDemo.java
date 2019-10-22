package main.concurrency;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Simple sample of Producer/Consumer using ArrayBlockingQueue (ThreadSafe)
 * - One Producer adds items in a pipeline in a frequency of 100ms
 * - The pipeline has a limit of 5 items
 * - When the pipeline is full, the Producer will wait for 500ms
 * - Two consumers take items from the pipeline and consumes it in 500ms
 */
public class BlockingQueueDemo {

    private static final String STOP = "producer stopped";
    private static final int CONSUMERS = 2;
    private static final int CAPACITY = 5;

    static class Producer extends Thread {
        BlockingQueue pipeline;

        Producer(BlockingQueue pipeline) {
            this. pipeline = pipeline;
        }

        public void run() {
            int nItems = 1;
            while (nItems <= 20) {
                try {
                    if (pipeline.remainingCapacity() != 0) {
                        String item = "item" + nItems;
                        pipeline.add(item);
                        String capacity = String.format(" [%d/%d]", pipeline.size(), CAPACITY);
                        System.out.println("Producer is adding " + item + capacity );
                        nItems++;
                        Thread.sleep(100);
                    } else {
                        System.out.println("Producer queue is full");
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for (int i = 0; i < CONSUMERS; i++) {
                pipeline.add(STOP);
            }
        }
    }

    static class Consumer extends Thread {
        BlockingQueue pipeline;
        String name;

        Consumer(String name, BlockingQueue pipeline) {
            this.name = name;
            this. pipeline = pipeline;
        }

        public void run() {
            while (true) {
                try {
                    String item = (String)pipeline.take();
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

    public static void main(String[] args) {
        BlockingQueue pipeline = new ArrayBlockingQueue<String>(CAPACITY);
        new Producer(pipeline).start();
        for (int i = 0; i < CONSUMERS; i++) {
            new Consumer("Consumer" + i, pipeline).start();
        }
    }
}
