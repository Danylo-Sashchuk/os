import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class UserTask extends Thread{
    private static AtomicInteger taskCounter = new AtomicInteger(0);
    private int taskId;
    private int totalExecutionUnits;
    private int remainingExecutionUnits;
    private Random rand;
    private int stsQueueId;
    private Semaphore executionSemaphore;
    private STSQueue[] stsQueues;
    private LongTermScheduler lts;

    public UserTask(STSQueue[] stsQueues, LongTermScheduler lts) {
        this.taskId = taskCounter.incrementAndGet();
        this.rand = new Random();
        this.totalExecutionUnits = rand.nextInt(25) + 1;
        this.remainingExecutionUnits = totalExecutionUnits;
        this.stsQueues = stsQueues;
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

    public void executeOnProcessor(int timeUnits, int processorId) {
        int executedUnits = Math.min(timeUnits, remainingExecutionUnits);
        remainingExecutionUnits -= executedUnits;

        System.out.println("User Task " + taskId + " is executing on Processor " + processorId
                           + " for " + executedUnits + " units. Remaining units: " + remainingExecutionUnits);

        try {
            Thread.sleep(executedUnits * 300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (remainingExecutionUnits > 0) {
            System.out.println("User Task " + taskId + " re-entering STS Queue " + stsQueueId
                               + " with remaining units: " + remainingExecutionUnits);

            stsQueues[stsQueueId].enqueueTask(this);
        } else {
            executionSemaphore.release();
        }
    }

    public int getRemainingExecutionUnits() {
        return remainingExecutionUnits;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setStsQueueId(int id) {
        this.stsQueueId = id;
    }
}
