public class LongTermScheduler {
    private ShortTermScheduler[] schedulers;
    private int nextQueueIndex;
    private long waitTime;

    public LongTermScheduler(ShortTermScheduler[] schedulers, long waitTime) {
        this.schedulers = schedulers;
        this.nextQueueIndex = 0;
        this.waitTime = waitTime;
    }

    public void enterSystem(UserTask task) {
        boolean enteredSTS = false;
        while (!enteredSTS) {
            ShortTermScheduler scheduler = schedulers[nextQueueIndex];
            nextQueueIndex = (nextQueueIndex + 1) % schedulers.length;

            STSQueue stsQueue = scheduler.getStsQueue();

            if (stsQueue.capacitySemaphore.availablePermits() > 0) {
                task.setStsQueueId(stsQueue.getQueueId());
                stsQueue.enqueueTask(task, true);

                scheduler.notifyTaskAvailable();
                enteredSTS = true;
            } else {
                System.out.println("User Task " + task.getTaskId() + " could not enter STS Queue "
                                   + stsQueue.getQueueId() + " (queue full), waiting...");
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
