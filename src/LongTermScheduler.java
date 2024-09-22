import java.util.Random;

/**
 * @author Danylo Sashchuk <p>
 * 9/22/24
 */

public class LongTermScheduler {
    private STSQueue[] stsQueues;
    private int nextQueueIndex;

    public LongTermScheduler(STSQueue[] stsQueues) {
        this.stsQueues = stsQueues;
        this.nextQueueIndex = 0;
    }

    public void enterSystem(UserTask task) {
        boolean enteredSTS = false;
        while (!enteredSTS) {
            STSQueue stsQueue = stsQueues[nextQueueIndex];
            nextQueueIndex = (nextQueueIndex + 1) % stsQueues.length; // Move to the next queue

            if (stsQueue.capacitySemaphore.availablePermits() > 0) {
                task.setStsQueueId(stsQueue.getQueueId());
                stsQueue.enqueueTask(task);
                enteredSTS = true;
            } else {
                // STS queue is full, wait and try again
                System.out.println("User Task " + task.getTaskId() + " could not enter STS Queue "
                                   + stsQueue.getQueueId() + " (queue full), waiting...");
                try {
                    Thread.sleep(500); // Wait before trying again
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}