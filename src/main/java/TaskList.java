import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The user's list of tasks. Tasks are accessed by zero-based index; an index
 * outside the list is reported to the user using its one-based task number.
 */
public class TaskList {
    private final List<Task> tasks;

    public TaskList() {
        this(new ArrayList<>());
    }

    public TaskList(List<Task> tasks) {
        this.tasks = tasks;
    }

    public void add(Task task) {
        tasks.add(task);
    }

    /** @throws ShaheerException if there is no task at {@code index}. */
    public Task get(int index) throws ShaheerException {
        checkIndex(index);
        return tasks.get(index);
    }

    /**
     * Removes and returns the task at {@code index}.
     *
     * @throws ShaheerException if there is no task at {@code index}.
     */
    public Task delete(int index) throws ShaheerException {
        checkIndex(index);
        return tasks.remove(index);
    }

    public int size() {
        return tasks.size();
    }

    /** Returns a read-only view of the tasks, for displaying or saving them. */
    public List<Task> getAll() {
        return Collections.unmodifiableList(tasks);
    }

    private void checkIndex(int index) throws ShaheerException {
        if (index < 0 || index >= tasks.size()) {
            throw new ShaheerException("There is no task number " + (index + 1) + " in your list.");
        }
    }
}
