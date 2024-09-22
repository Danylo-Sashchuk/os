import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Initialize STS queues
        STSQueue[] stsQueues = new STSQueue[2];
        stsQueues[0] = new STSQueue(0);
        stsQueues[1] = new STSQueue(1);

        // Initialize Long-Term Scheduler
        LongTermScheduler lts = new LongTermScheduler(stsQueues);

        // Initialize Processor Coordinator and start processors
        ProcessorCoordinator coordinator = ProcessorCoordinator.getInstance(stsQueues);
        coordinator.startProcessors();

        // Create and start user tasks
        List<UserTask> userTasks = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            UserTask task = new UserTask(stsQueues, lts);
            userTasks.add(task);
            task.start();

            // Simulate random arrival of tasks
            try {
                Thread.sleep(new Random().nextInt(500)); // Up to 0.5 second
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Wait for all user tasks to complete
        for (UserTask task : userTasks) {
            try {
                task.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Since processors are in infinite loops, we can terminate the program here
        System.out.println("All user tasks have completed execution.");
        System.exit(0);
    }
}