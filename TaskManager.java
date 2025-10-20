import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TaskManager {
    private List<Task> tasks = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        TaskManager manager = new TaskManager();
        manager.run();
    }

    public void run() {
        while (true) {
            System.out.println("\n1. Add Task\n2. View Tasks\n3. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addTask();
                    break;
                case 2:
                    // Todo: Implement view tasks
                    break;
                case 3:
                    System.out.println("Exiting...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    private void viewTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks available.");
        } else {
            System.out.println("\nTasks:");
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                System.out.println((i + 1) + ". " + task.getDescription() + " (Priority: " + task.getPriority() + ")");
            }
        }
    }
    private void addTask() {
        String description;
        do {
            System.out.print("Enter task description: ");
            description = scanner.nextLine();
            if (description.isEmpty()) {
                System.out.println("Description cannot be empty. Please try again.");
            }
        } while (description.isEmpty());

        System.out.print("Enter priority (1: High, 2: Medium, 3: Low): ");
        int priority = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        tasks.add(new Task(description, priority));
        System.out.println("Task added.");
    }

}
