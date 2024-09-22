import java.util.concurrent.Semaphore;

/**
 * @author Danylo Sashchuk <p>
 * 9/22/24
 */

public class ProcessorCoordinator {
    private static ProcessorCoordinator instance = null;
    private STSQueue[] stsQueues;
    private Processor[] processors;
    private Semaphore[] processorSemaphores; // Semaphores to signal processors when tasks are available

    private ProcessorCoordinator(STSQueue[] stsQueues) {
        this.stsQueues = stsQueues;
        this.processorSemaphores = new Semaphore[stsQueues.length];
        for (int i = 0; i < stsQueues.length; i++) {
            processorSemaphores[i] = new Semaphore(0); // Start with zero permits
        }

        // Initialize processors
        processors = new Processor[stsQueues.length * 4]; // Four processors per STS queue
        int processorId = 1;
        for (int i = 0; i < stsQueues.length; i++) {
            for (int j = 0; j < 4; j++) {
                processors[(i * 4) + j] = new Processor(processorId++, stsQueues[i], processorSemaphores[i]);
            }
        }
    }

    public static ProcessorCoordinator getInstance(STSQueue[] stsQueues) {
        if (instance == null) {
            instance = new ProcessorCoordinator(stsQueues);
        }
        return instance;
    }

    public static ProcessorCoordinator getInstance() {
        return instance;
    }

    public void startProcessors() {
        for (Processor processor : processors) {
            processor.start();
        }
    }

    public void notifyTaskAvailable(int queueId) {
        // Release a permit to signal that a task is available
        processorSemaphores[queueId].release();
    }
}
