import java.util.ArrayList;
import java.util.List;

/**
 * Entry point of the Shaheer chatbot, which manages a list of tasks
 * (todos, deadlines and events) and saves them between sessions.
 */
public class Shaheer {
    private static final String FILE_PATH = "data/shaheer.txt";

    private final Ui ui;
    private final Storage storage;
    private final List<Task> tasks;

    public Shaheer(String filePath) {
        ui = new Ui();
        storage = new Storage(filePath);
        tasks = loadTasks();
    }

    public static void main(String[] args) {
        new Shaheer(FILE_PATH).run();
    }

    /** Reads and executes commands until the user exits, then saves the tasks. */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit && ui.hasNextCommand()) {
            String input = ui.readCommand();
            ui.showLine();
            isExit = executeCommand(input);
            ui.showLine();
        }
        saveTasks();
    }

    // ---------------------------------------------------------------- Command execution

    /**
     * Runs the command in one line of user input and reports any input error to the user.
     *
     * @return true if the user asked to exit.
     */
    private boolean executeCommand(String input) {
        String commandWord = getCommandWord(input);
        String args = getArguments(input);
        try {
            switch (commandWord) {
            case "bye":
                ui.showGoodbye();
                return true;
            case "list":
                ui.showTaskList(tasks);
                break;
            case "mark":
                markTask(args);
                break;
            case "unmark":
                unmarkTask(args);
                break;
            case "delete":
                deleteTask(args);
                break;
            case "todo":
                addTask(parseTodo(args));
                break;
            case "deadline":
                addTask(parseDeadline(args));
                break;
            case "event":
                addTask(parseEvent(args));
                break;
            default:
                throw new ShaheerException("I'm sorry, but I don't know what that means");
            }
        } catch (ShaheerException e) {
            ui.showError(e.getMessage());
        }
        return false;
    }

    private void markTask(String args) throws ShaheerException {
        Task task = tasks.get(parseTaskIndex(tasks, args, "mark"));
        task.markAsDone();
        ui.showTaskMarked(task);
    }

    private void unmarkTask(String args) throws ShaheerException {
        Task task = tasks.get(parseTaskIndex(tasks, args, "unmark"));
        task.markAsNotDone();
        ui.showTaskUnmarked(task);
    }

    private void deleteTask(String args) throws ShaheerException {
        Task task = tasks.remove(parseTaskIndex(tasks, args, "delete"));
        ui.showTaskDeleted(task, tasks.size());
    }

    private void addTask(Task task) {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
    }

    // ---------------------------------------------------------------- Parsing

    private static String getCommandWord(String input) {
        return input.split(" ", 2)[0].toLowerCase();
    }

    /** Returns the text after the command word, or an empty string if there is none. */
    private static String getArguments(String input) {
        String[] parts = input.split(" ", 2);
        return parts.length < 2 ? "" : parts[1].trim();
    }

    /**
     * Converts the one-based task number in {@code args} into a zero-based list index.
     *
     * @param commandWord the command being run, used to show an example of correct input.
     * @throws ShaheerException if the number is missing, not a whole number, or out of range.
     */
    private static int parseTaskIndex(List<Task> tasks, String args, String commandWord)
            throws ShaheerException {
        if (args.isEmpty()) {
            throw new ShaheerException("Please specify the task number, e.g.: " + commandWord + " 2");
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(args);
        } catch (NumberFormatException e) {
            throw new ShaheerException("The task number must be a whole number, e.g.: " + commandWord + " 2");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new ShaheerException("There is no task number " + taskNumber + " in your list.");
        }
        return taskNumber - 1;
    }

    /** @throws ShaheerException if the description is blank. */
    private static Todo parseTodo(String args) throws ShaheerException {
        if (args.isEmpty()) {
            throw new ShaheerException("The description of a todo cannot be empty.");
        }
        return new Todo(args);
    }

    /**
     * Builds a Deadline from arguments of the form "{@code <description> /by <when>}".
     *
     * @throws ShaheerException if the description, "/by", or the time is missing.
     */
    private static Deadline parseDeadline(String args) throws ShaheerException {
        String[] deadlineParts = args.split(" /by ", 2);
        if (deadlineParts.length < 2 || deadlineParts[0].trim().isEmpty() || deadlineParts[1].trim().isEmpty()) {
            throw new ShaheerException(
                "A deadline needs a description and a /by time, e.g.: deadline return book /by Sunday");
        }
        return new Deadline(deadlineParts[0].trim(), deadlineParts[1].trim());
    }

    /**
     * Builds an Event from arguments of the form "{@code <description> /from <start> /to <end>}".
     *
     * @throws ShaheerException if the description, "/from", or "/to" part is missing.
     */
    private static Event parseEvent(String args) throws ShaheerException {
        String[] eventParts = args.split(" /from ", 2);
        if (eventParts.length < 2 || eventParts[0].trim().isEmpty()) {
            throw new ShaheerException(
                "An event needs a description, a /from time and a /to time, e.g.: "
                    + "event project meeting /from Mon 2pm /to 4pm");
        }
        String[] eventTimeParts = eventParts[1].split(" /to ", 2);
        if (eventTimeParts.length < 2 || eventTimeParts[0].trim().isEmpty() || eventTimeParts[1].trim().isEmpty()) {
            throw new ShaheerException(
                "An event needs both a /from time and a /to time, e.g.: "
                    + "event project meeting /from Mon 2pm /to 4pm");
        }
        return new Event(eventParts[0].trim(), eventTimeParts[0].trim(), eventTimeParts[1].trim());
    }

    // ---------------------------------------------------------------- Storage

    private List<Task> loadTasks() {
        try {
            return storage.load();
        } catch (ShaheerException e) {
            ui.showStorageError(e.getMessage());
            return new ArrayList<>();
        }
    }

    private void saveTasks() {
        try {
            storage.save(tasks);
        } catch (ShaheerException e) {
            ui.showStorageError(e.getMessage());
        }
    }
}
