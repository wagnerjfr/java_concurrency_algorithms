import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Simple example of using Semaphore[1] to control access to shared resources
 * [1] Can be used by multiple threads at the same time and includes a counter to track availability
 */
public class SemaphoreDemo {
    private static final Semaphore charger = new Semaphore(5);

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new ElectricalVehicle("EV" + i).start();
        }
    }

    private static class ElectricalVehicle extends Thread {
        private final String name;

        ElectricalVehicle(String name) {
            this.name = name;
        }

        public void run() {
            try {
                charger.acquire();
                System.out.println(name + " is charging..");
                Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 4000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(name + " finished charging.");
                charger.release();
            }
        }
    }
}
