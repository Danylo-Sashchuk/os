import java.util.concurrent.Semaphore;

public class ShortTermScheduler extends Thread {
    private STSQueue stsQueue;
    private Processor[] processors;
    private Semaphore processorSemaphore; // signal when tasks are available

    public ShortTermScheduler(int schedulerId, int queueCapacity, long executionTimePerUnit) {
        this.stsQueue = new STSQueue(schedulerId, queueCapacity);
        this.processorSemaphore = new Semaphore(0);

        processors = new Processor[4];
        for (int i = 0; i < 4; i++) {
            processors[i] = new Processor(i, executionTimePerUnit);
            processors[i].start();
        }
    }

    public STSQueue getStsQueue() {
        return stsQueue;
    }

    public void notifyTaskAvailable() {
        // permit to signal that task available
        processorSemaphore.release();
    }

    @Override
    public void run() {
        while (true) {
            try {
                processorSemaphore.acquire();

                UserTask task = stsQueue.dequeueTask();
                if (task != null) {
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
                            Thread.sleep(100);
                        }
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}