public class Task {
    private String description;
    private int priority; // 1: High, 2: Medium, 3: Low

    public Task(String description, int priority) {
        this.description = description;
        this.priority = priority;
    }
    public int getPriority() {
        return priority;
    }

    public String getDescription() {
        return description;
    }
}
