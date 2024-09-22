import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) {
        int queueCapacity = 10;
        long executionTimePerUnit = 1000L;
        int taskArrivalTimeBound = 100;
        int numberOfTasks = 60;
        long waitTime = 5000L;

        // Parse command-line arguments
        try {
            if (args.length >= 1) {
                queueCapacity = Integer.parseInt(args[0]);
            }
            if (args.length >= 2) {
                numberOfTasks = Integer.parseInt(args[1]);
            }
            if (args.length >= 3) {
                executionTimePerUnit = Long.parseLong(args[2]);
            }
            if (args.length >= 4) {
                taskArrivalTimeBound = Integer.parseInt(args[3]);
            }
            if (args.length >= 5) {
                waitTime = Long.parseLong(args[4]);
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid arguments. Using default values.");
        }

        System.out.println("Queue Capacity: " + queueCapacity);
        System.out.println("Number of Tasks: " + numberOfTasks);
        System.out.println("Execution Time Per Unit: " + executionTimePerUnit + " ms");
        System.out.println("Task Arrival Time Bound: " + taskArrivalTimeBound + " ms");
        System.out.println("Wait time for pushing into STS: " + waitTime + " ms");

        // Initialize Short-Term Schedulers
        ShortTermScheduler[] schedulers = new ShortTermScheduler[2];
        // Pass queueCapacity and executionTimePerUnit to the schedulers
        schedulers[0] = new ShortTermScheduler(0, queueCapacity, executionTimePerUnit);
        schedulers[1] = new ShortTermScheduler(1, queueCapacity, executionTimePerUnit);

        // Start the Short-Term Schedulers
        schedulers[0].start();
        schedulers[1].start();

        // Initialize Long-Term Scheduler
        LongTermScheduler lts = new LongTermScheduler(schedulers, waitTime);

        // Create and start user tasks
        List<UserTask> userTasks = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < numberOfTasks; i++) {
            UserTask task = new UserTask(schedulers, lts);
            userTasks.add(task);
            task.start();

            // Simulate random arrival of tasks
            try {
                Thread.sleep(random.nextInt(taskArrivalTimeBound)); // Up to taskArrivalTimeBound milliseconds
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
