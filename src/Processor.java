import java.util.concurrent.Semaphore;

public class Processor extends Thread {
    private int processorId;
    private ShortTermScheduler scheduler;
    private UserTask currentTask;
    private int timeQuantum;
    private Semaphore taskAssignedSemaphore;
    private Semaphore idleSemaphore;

    public Processor(int processorId, ShortTermScheduler scheduler) {
        this.processorId = processorId;
        this.scheduler = scheduler;
        this.timeQuantum = ProcessorTimeQuantum.getTimeQuantum(processorId);
        this.taskAssignedSemaphore = new Semaphore(0);
        this.idleSemaphore = new Semaphore(1); // Starts as idle
    }

    public boolean isIdle() {
        return idleSemaphore.availablePermits() > 0;
    }

    public void assignTask(UserTask task) {
        try {
            // Wait until processor is idle
            idleSemaphore.acquire();
            this.currentTask = task;
            // Signal the processor that a task is assigned
            taskAssignedSemaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                // Wait until a task is assigned
                taskAssignedSemaphore.acquire();

                // Execute the task
                currentTask.executeOnProcessor(timeQuantum, processorId);

                // Mark the processor as idle
                currentTask = null;
                idleSemaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
