import java.util.concurrent.Semaphore;

public class Processor extends Thread {
    private int processorId;
    private UserTask currentTask;
    private int timeQuantum;
    private Semaphore taskAssignedSemaphore;
    private Semaphore idleSemaphore;
    private long executionTimePerUnit;

    public Processor(int processorId, long executionTimePerUnit) {
        this.processorId = processorId;
        this.timeQuantum = getTimeQuantum(processorId);
        this.taskAssignedSemaphore = new Semaphore(0);
        this.idleSemaphore = new Semaphore(1);
        this.executionTimePerUnit = executionTimePerUnit;
    }

    public boolean isIdle() {
        return idleSemaphore.availablePermits() > 0;
    }

    public void assignTask(UserTask task) {
        try {
            // wait until processor is idle
            idleSemaphore.acquire();
            this.currentTask = task;
            // signal the processor that a task is assigned
            taskAssignedSemaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                taskAssignedSemaphore.acquire();

                currentTask.executeOnProcessor(timeQuantum, processorId, executionTimePerUnit);

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
