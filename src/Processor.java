import java.util.concurrent.Semaphore;

public class Processor extends Thread {
    private int processorId;
    private ShortTermScheduler scheduler;
    private UserTask currentTask;
    private int timeQuantum;
    private Semaphore taskAssignedSemaphore;
    private Semaphore idleSemaphore;
    private long executionTimePerUnit; // Added

    public Processor(int processorId, ShortTermScheduler scheduler, long executionTimePerUnit) {
        this.processorId = processorId;
        this.scheduler = scheduler;
        this.timeQuantum = getTimeQuantum(processorId);
        this.taskAssignedSemaphore = new Semaphore(0);
        this.idleSemaphore = new Semaphore(1); // Starts as idle
        this.executionTimePerUnit = executionTimePerUnit; // Added
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
                currentTask.executeOnProcessor(timeQuantum, processorId, executionTimePerUnit);

                // Mark the processor as idle
                currentTask = null;
                idleSemaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private int getTimeQuantum(int processorId) {
        return switch (processorId) {
            case 2 -> 4;
            case 3 -> 3;
            default -> 2;
        };
    }
}
