# Release flow demonstration
This repository is a demonstration of a release flow through a simple Task Manager project in Java.

## About the project
The project is a simple Task Manager that allows the user to create, read, update, and delete tasks from the command line.

## Steps

### 0. Starter project
First, start by forking this repository and cloning it to your local machine.
```bash
git clone https://github.com/VoidKeishi/ReleaseFlowDemonstration.git
cd ReleaseFlowDemonstration
git checkout starter
```

### 1. Add view task feature

Checkout a new branch for the feature.
```bash
git checkout -b feature/view-tasks
```

Add this feature to the `TaskManager` class:
```java
private void viewTasks() {
    if (tasks.isEmpty()) {
        System.out.println("No tasks available.");
    } else {
        System.out.println("\nTasks:");
        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            System.out.println((i + 1) + ". " + task.getDescription());
        }
    }
}
```

Test the feature by running the project:
```bash
javac TaskManager.java
java TaskManager
```

Notice that it will generate class files in the project directory. Add the class files to the `.gitignore` file:
```
*.class
```

Commit the changes and push the branch to the remote repository:
```bash
git add .
git commit -m "Add view tasks feature"
git push origin feature/view-tasks
```

Create a pull request on GitHub and merge the feature branch into the `main` branch. After the pull request is merged, checkout the `main` branch and pull the changes:
```bash
git checkout main
git pull
```

### 2. Add functionality to assign priority levels to tasks

Checkout a new branch for the job. Remember to pull the latest changes from the `main` branch:
```bash
git checkout -b topic/task-priority
```

Add the priority field to the `Task` class:
```java
private int priority; // 1: High, 2: Medium, 3: Low
```

Modify the constructor to include the priority field:
```java
public Task(String description, int priority) {
    this.description = description;
    this.priority = priority;
}
```

Add a getter method for the priority field:
```java
public int getPriority() {
    return priority;
}
```

Update the `addTask` method to include priority:
```java
private void addTask() {
    System.out.print("Enter task description: ");
    String description = scanner.nextLine();
    System.out.print("Enter priority (1: High, 2: Medium, 3: Low): ");
    int priority = scanner.nextInt();
    scanner.nextLine(); // Consume newline
    tasks.add(new Task(description, priority));
    System.out.println("Task added.");
}
```

Update the `viewTasks` method to display the priority of each task:
```java
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
```

Test the feature by running the project, committing the changes, pushing the branch, creating a pull request, merging the pull request, and pulling the changes to the `main` branch.

### 3. Fix a bug where tasks with empty descriptions can be added

Checkout a new branch for the bug fix. Remember to pull the latest changes from the `main` branch:
```bash
git checkout -b bugfix/empty-task-description
```

Update the `addTask` method to check for empty descriptions:
```java
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
```

Test the feature by running the project, committing the changes, pushing the branch, creating a pull request, merging the pull request, and pulling the changes to the `main` branch.

### 4. Release version 1.0

After finishing some basic features, it's time to release the first version of the project. Start by creating a release branch from the `main` branch:
```bash
git checkout -b release/1.0
git push origin release/1.0
```

Now users can download the source code from the release branch and run the project.

## Additional notes
If you forgot to pull the latest changes from the remote branch before committing your changes, you can pull the changes if there are no conflicts with rebasing:
```bash
git pull --rebase
```

If there are conflicts, you will need to resolve them manually.

## Benefits of Release Flow

1. **Organized Development**: The use of branches allows developers to work on multiple features and bug fixes simultaneously without interfering with the stable `main` branch.
2. **Improved Collaboration**: Pull requests facilitate code reviews, ensuring better quality code and reducing the likelihood of introducing bugs.
3. **Clear Versioning**: Release branches provide a clear structure for versioning, making it easy to track and manage changes for each version.
4. **Reduced Risk**: By isolating changes into feature or bugfix branches, there is less risk of destabilizing the project.
5. **Faster Rollbacks**: If a release introduces critical issues, it is easier to identify and revert changes.

## Conclusion
This demonstration shows a simple release flow using branches, pull requests, and versioning. The release flow allows for the development of new features, bug fixes, and releases in a structured and organized manner.

