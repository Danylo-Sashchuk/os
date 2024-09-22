import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class STSQueue {
    private List<UserTask> taskList;
    private Semaphore capacitySemaphore;
    private Semaphore accessSemaphore;
    private int queueId;
    private Semaphore taskAvailableSemaphore;

    public STSQueue(int queueId) {
        this.queueId = queueId;
        this.taskList = new LinkedList<>();
        this.capacitySemaphore = new Semaphore(15);
        this.accessSemaphore = new Semaphore(1);
        this.taskAvailableSemaphore = new Semaphore(0);
    }

    public void enqueueTask(UserTask task, boolean isNewTask) {
        try {
            if (isNewTask) {
                capacitySemaphore.acquire();
                task.setArrivalTime(System.currentTimeMillis());
            }

            accessSemaphore.acquire();

            taskList.add(task);
            taskList.sort(Comparator.comparingDouble(UserTask::getPriorityAge));

            accessSemaphore.release();
            taskAvailableSemaphore.release();

            System.out.println("User Task " + task.getTaskId() + " entered STS Queue " + queueId
                               + ". Queue has " + taskList.size() + " tasks.");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public UserTask dequeueTask() {
        UserTask task = null;
        try {
            taskAvailableSemaphore.acquire();
            accessSemaphore.acquire();

            if (!taskList.isEmpty()) {
                task = taskList.remove(0);
            }

            accessSemaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return task;
    }

    public void releaseCapacityPermit() {
        capacitySemaphore.release();
        System.out.println("CapacitySemaphore released by Task completion in STS Queue " + queueId
                           + ". Available permits: " + capacitySemaphore.availablePermits());
    }

    public int getQueueId() {
        return queueId;
    }
}
