import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        // Initialize Short-Term Schedulers
        ShortTermScheduler[] schedulers = new ShortTermScheduler[2];
        schedulers[0] = new ShortTermScheduler(0);
        schedulers[1] = new ShortTermScheduler(1);

        // Start the Short-Term Schedulers
        schedulers[0].start();
        schedulers[1].start();

        // Initialize Long-Term Scheduler
        LongTermScheduler lts = new LongTermScheduler(schedulers);

        // Create and start user tasks
        List<UserTask> userTasks = new ArrayList<>();
        for (int i = 0; i < 60; i++) {
            UserTask task = new UserTask(schedulers, lts);
            userTasks.add(task);
            task.start();

            // Simulate random arrival of tasks
            try {
                Thread.sleep(new Random().nextInt(100)); // Up to 100 milliseconds
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
