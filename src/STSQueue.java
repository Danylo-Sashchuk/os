import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * @author Danylo Sashchuk <p>
 * 9/22/24
 */

public class STSQueue {
    private List<UserTask> taskList;
    Semaphore capacitySemaphore;
    private Semaphore accessSemaphore;
    private int queueId;
    private Semaphore taskAvailableSemaphore; // New semaphore

    public STSQueue(int queueId) {
        this.queueId = queueId;
        this.taskList = new LinkedList<>();
        this.capacitySemaphore = new Semaphore(15);
        this.accessSemaphore = new Semaphore(1);
        this.taskAvailableSemaphore = new Semaphore(0); // In
    }

    public void enqueueTask(UserTask task, boolean isNewTask) {
        try {
            if (isNewTask) {
                capacitySemaphore.acquire();
                task.setArrivalTime(System.currentTimeMillis());
            }

            accessSemaphore.acquire();

            taskList.add(task);
            taskList.sort(Comparator.comparingDouble(UserTask::getPriority));

            accessSemaphore.release();
            taskAvailableSemaphore.release();

            System.out.println("User Task " + task.getTaskId() + " entered STS " + queueId
                               + ". STS has " + taskList.size() + " tasks.");

            ProcessorCoordinator.getInstance().notifyTaskAvailable(queueId);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void releaseCapacityPermit() {
        capacitySemaphore.release();
        System.out.println("CapacitySemaphore released by Task completion in STS Queue " + queueId
                           + ". Available permits: " + capacitySemaphore.availablePermits());
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

    public int getQueueId() {
        return queueId;
    }

    public boolean isEmpty() {
        boolean empty = false;
        try {
            accessSemaphore.acquire();
            empty = taskList.isEmpty();
            accessSemaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return empty;
    }

    public List<UserTask> getTaskList() {
        return taskList;
    }
}
