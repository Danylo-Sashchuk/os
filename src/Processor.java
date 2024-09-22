import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * @author Danylo Sashchuk <p>
 * 9/22/24
 */

public class Processor extends Thread {
    private static Map<Integer, Integer> processorTimeQuantumMap = new HashMap<>();

    static {
        // Initialize processor time quantums
        processorTimeQuantumMap.put(1, 2);
        processorTimeQuantumMap.put(2, 4);
        processorTimeQuantumMap.put(3, 3);
        processorTimeQuantumMap.put(4, 2);
        processorTimeQuantumMap.put(5, 2);
        processorTimeQuantumMap.put(6, 4);
        processorTimeQuantumMap.put(7, 3);
        processorTimeQuantumMap.put(8, 2);
    }

    private int processorId;
    private STSQueue stsQueue;
    private Semaphore processorSemaphore;
    private int timeQuantum;

    public Processor(int processorId, STSQueue stsQueue, Semaphore processorSemaphore) {
        this.processorId = processorId;
        this.stsQueue = stsQueue;
        this.processorSemaphore = processorSemaphore;
        this.timeQuantum = processorTimeQuantumMap.getOrDefault(processorId, 2);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Wait until a task is available
                processorSemaphore.acquire();

                UserTask task = null;
                while (task == null) {
                    task = stsQueue.dequeueTask();
                    if (task == null) {
                        // No task available, wait again
                        processorSemaphore.acquire();
                    }
                }

                // Execute the task
                task.executeOnProcessor(timeQuantum, processorId);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
