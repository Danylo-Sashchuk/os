import java.util.concurrent.Semaphore;

public class ShortTermScheduler extends Thread {
    private STSQueue stsQueue;
    private Processor[] processors;
    private Semaphore processorSemaphore; // To signal when tasks are available

    public ShortTermScheduler(int schedulerId) {
        this.stsQueue = new STSQueue(schedulerId);
        this.processorSemaphore = new Semaphore(0);

        // Initialize four processors for this STS
        processors = new Processor[4];
        for (int i = 0; i < 4; i++) {
            int processorId = (schedulerId * 4) + i + 1;
            processors[i] = new Processor(processorId, this);
            processors[i].start();
        }
    }

    public STSQueue getStsQueue() {
        return stsQueue;
    }

    public void notifyTaskAvailable() {
        // Release a permit to signal that a task is available
        processorSemaphore.release();
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Wait until tasks are available
                processorSemaphore.acquire();

                UserTask task = stsQueue.dequeueTask();
                if (task != null) {
                    // Assign task to an available processor
                    boolean assigned = false;
                    while (!assigned) {
                        for (Processor processor : processors) {
                            if (processor.isIdle()) {
                                processor.assignTask(task);
                                assigned = true;
                                break;
                            }
                        }
                        if (!assigned) {
                            Thread.sleep(100); // Wait before checking again
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}