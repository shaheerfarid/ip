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

    /** Returns "X" if this task is done, or a blank space otherwise. */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
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
