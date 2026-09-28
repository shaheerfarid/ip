/**
 * Represents a single task with a description and a done/not-done status.
 */
public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public String getDescription() {
        return description;
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsNotDone() {
        isDone = false;
    }

    /** Formats the task as "[X] description" or "[ ] description". */
    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}