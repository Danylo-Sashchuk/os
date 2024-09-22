public class LongTermScheduler {
    private ShortTermScheduler[] schedulers;
    private int nextQueueIndex;

    public LongTermScheduler(ShortTermScheduler[] schedulers) {
        this.schedulers = schedulers;
        this.nextQueueIndex = 0;
    }

    public void enterSystem(UserTask task) {
        boolean enteredSTS = false;
        while (!enteredSTS) {
            ShortTermScheduler scheduler = schedulers[nextQueueIndex];
            nextQueueIndex = (nextQueueIndex + 1) % schedulers.length; // Move to the next scheduler

            STSQueue stsQueue = scheduler.getStsQueue();

            if (stsQueue.capacitySemaphore.availablePermits() > 0) {
                task.setStsQueueId(stsQueue.getQueueId());
                stsQueue.enqueueTask(task, true);
                // Notify the STS that a task is available
                scheduler.notifyTaskAvailable();
                enteredSTS = true;
            } else {
                // STS queue is full, wait and try again
                System.out.println("User Task " + task.getTaskId() + " could not enter STS Queue "
                                   + stsQueue.getQueueId() + " (queue full), waiting...");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
