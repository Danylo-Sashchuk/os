import java.util.Random;
import java.util.concurrent.Semaphore;

public class UserTask extends Thread {
    private static int taskCounter = 0;
    private static Semaphore taskCounterSemaphore = new Semaphore(1);
    private int taskId;
    private int totalExecutionUnits;
    private int remainingExecutionUnits;
    private Random rand;
    private int stsQueueId;
    private Semaphore executionSemaphore;
    private ShortTermScheduler[] schedulers;
    private LongTermScheduler lts;
    private long arrivalTime;

    public UserTask(ShortTermScheduler[] schedulers, LongTermScheduler lts) {
        try {
            taskCounterSemaphore.acquire();
            this.taskId = ++taskCounter;
            taskCounterSemaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.rand = new Random();
        this.totalExecutionUnits = rand.nextInt(25) + 1;
        this.remainingExecutionUnits = totalExecutionUnits;
        this.schedulers = schedulers;
        this.lts = lts;
        this.executionSemaphore = new Semaphore(0);
    }

    @Override
    public void run() {
        try {
            int sleepTime = rand.nextInt(5000);
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("User Task " + taskId + " arrived with total execution units: " + totalExecutionUnits);

        lts.enterSystem(this);

        try {
            executionSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("User Task " + taskId + " has completed execution and is leaving the system.");
    }

    public void executeOnProcessor(int timeUnits, int processorId, long executionTimePerUnit) {
        int executedUnits = Math.min(timeUnits, remainingExecutionUnits);
        remainingExecutionUnits -= executedUnits;

        System.out.println("User Task " + taskId + " is executing on Processor " + processorId
                           + " for " + executedUnits + " units. Remaining units: " + remainingExecutionUnits);

        try {
            Thread.sleep(executedUnits * executionTimePerUnit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (remainingExecutionUnits > 0) {
            System.out.println("User Task " + taskId + " re-entering STS Queue " + stsQueueId
                               + " with remaining units: " + remainingExecutionUnits);

            schedulers[stsQueueId].getStsQueue().enqueueTask(this, false);
            schedulers[stsQueueId].notifyTaskAvailable();
        } else {
            executionSemaphore.release();
            schedulers[stsQueueId].getStsQueue().releaseCapacityPermit();
        }
    }

    public double getPriorityAge() {
        long waitingTime = System.currentTimeMillis() - arrivalTime;
        return remainingExecutionUnits - (waitingTime / 1000.0);
    }

    public int getTaskId() {
        return taskId;
    }

    public void setStsQueueId(int id) {
        this.stsQueueId = id;
    }

    public void setArrivalTime(long arrivalTime) {
        this.arrivalTime = arrivalTime;
    }
}
