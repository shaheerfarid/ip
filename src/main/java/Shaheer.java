/**
 * Entry point of the Shaheer chatbot, which manages a list of tasks
 * (todos, deadlines and events) and saves them between sessions.
 */
public class Shaheer {
    private static final String FILE_PATH = "data/shaheer.txt";

    private final Ui ui;
    private final Storage storage;
    private final TaskList tasks;

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
        String commandWord = Parser.getCommandWord(input);
        String args = Parser.getArguments(input);
        try {
            switch (commandWord) {
            case "bye":
                ui.showGoodbye();
                return true;
            case "list":
                ui.showTaskList(tasks.getAll());
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
                addTask(Parser.parseTodo(args));
                break;
            case "deadline":
                addTask(Parser.parseDeadline(args));
                break;
            case "event":
                addTask(Parser.parseEvent(args));
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
        Task task = tasks.get(Parser.parseTaskIndex(args, "mark"));
        task.markAsDone();
        ui.showTaskMarked(task);
    }

    private void unmarkTask(String args) throws ShaheerException {
        Task task = tasks.get(Parser.parseTaskIndex(args, "unmark"));
        task.markAsNotDone();
        ui.showTaskUnmarked(task);
    }

    private void deleteTask(String args) throws ShaheerException {
        Task task = tasks.delete(Parser.parseTaskIndex(args, "delete"));
        ui.showTaskDeleted(task, tasks.size());
    }

    private void addTask(Task task) {
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
    }

    // ---------------------------------------------------------------- Storage

    private TaskList loadTasks() {
        try {
            return new TaskList(storage.load());
        } catch (ShaheerException e) {
            ui.showStorageError(e.getMessage());
            return new TaskList();
        }
    }

    private void saveTasks() {
        try {
            storage.save(tasks.getAll());
        } catch (ShaheerException e) {
            ui.showStorageError(e.getMessage());
        }
    }
}
