import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point of the Shaheer chatbot, which manages a list of tasks
 * (todos, deadlines and events) and saves them between sessions.
 */
public class Shaheer {
    private static final String DIVIDER = "____________________________________________________________";
    private static final Path STORAGE_PATH = Paths.get("data", "shaheer.txt");

    public static void main(String[] args) {
        printWelcome();
        List<Task> tasks = loadTasks();
        runCommandLoop(tasks);
        saveTasks(tasks);
    }

    // ---------------------------------------------------------------- Command loop

    private static void runCommandLoop(List<Task> tasks) {
        Scanner scanner = new Scanner(System.in);
        boolean isExit = false;
        while (!isExit && scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();
            System.out.println(DIVIDER);
            isExit = executeCommand(input, tasks);
            System.out.println(DIVIDER);
        }
    }

    /**
     * Runs the command in one line of user input and reports any input error to the user.
     *
     * @return true if the user asked to exit.
     */
    private static boolean executeCommand(String input, List<Task> tasks) {
        String commandWord = getCommandWord(input);
        String args = getArguments(input);
        try {
            switch (commandWord) {
            case "bye":
                printGoodbye();
                return true;
            case "list":
                listTasks(tasks);
                break;
            case "mark":
                markTask(tasks, args);
                break;
            case "unmark":
                unmarkTask(tasks, args);
                break;
            case "delete":
                deleteTask(tasks, args);
                break;
            case "todo":
                addTask(tasks, parseTodo(args));
                break;
            case "deadline":
                addTask(tasks, parseDeadline(args));
                break;
            case "event":
                addTask(tasks, parseEvent(args));
                break;
            default:
                throw new ShaheerException("I'm sorry, but I don't know what that means");
            }
        } catch (ShaheerException e) {
            printError(e);
        }
        return false;
    }

    // ---------------------------------------------------------------- Command handlers

    private static void listTasks(List<Task> tasks) {
        System.out.println("Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + "." + tasks.get(i));
        }
    }

    private static void markTask(List<Task> tasks, String args) throws ShaheerException {
        Task task = tasks.get(parseTaskIndex(tasks, args, "mark"));
        task.markAsDone();
        printTaskMessage("Nice! I've marked this task as done:", task);
    }

    private static void unmarkTask(List<Task> tasks, String args) throws ShaheerException {
        Task task = tasks.get(parseTaskIndex(tasks, args, "unmark"));
        task.markAsNotDone();
        printTaskMessage("OK, I've marked this task as not done yet:", task);
    }

    private static void deleteTask(List<Task> tasks, String args) throws ShaheerException {
        Task task = tasks.remove(parseTaskIndex(tasks, args, "delete"));
        printTaskMessage("Noted. I've removed this task:", task);
        printTaskCount(tasks);
    }

    private static void addTask(List<Task> tasks, Task task) {
        tasks.add(task);
        printTaskMessage("Got it. I've added this task:", task);
        printTaskCount(tasks);
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

    // ---------------------------------------------------------------- User interface

    private static void printWelcome() {
        String banner = " ____  _   _    _    _   _ _____ _____ ____  \n"
            + "/ ___|| | | |  / \\  | | | | ____| ____|  _ \\ \n"
            + "\\___ \\| |_| | / _ \\ | |_| |  _| |  _| | |_) |\n"
            + " ___) |  _  |/ ___ \\|  _  | |___| |___|  _ < \n"
            + "|____/|_| |_/_/   \\_\\_| |_|_____|_____|_| \\_\\\n";
        System.out.println(DIVIDER);
        System.out.println(banner);
        System.out.println("Hello, I am Shaheer.\nWhat can I do for you?");
        System.out.println(DIVIDER);
    }

    private static void printGoodbye() {
        System.out.println("Bye. Hope to see you back!");
    }

    private static void printTaskMessage(String message, Task task) {
        System.out.println(message);
        System.out.println("  " + task);
    }

    private static void printTaskCount(List<Task> tasks) {
        System.out.println("Now you have " + tasks.size() + " tasks in the list.");
    }

    private static void printError(ShaheerException e) {
        System.out.println("Hmmmm! " + e.getMessage());
    }

    // ---------------------------------------------------------------- Storage

    private static List<Task> loadTasks() {
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(STORAGE_PATH)) {
            return tasks;
        }
        try {
            for (String line : Files.readAllLines(STORAGE_PATH)) {
                Task task = decodeTask(line);
                if (task != null) {
                    tasks.add(task);
                }
            }
        } catch (IOException e) {
            System.out.println("I could not load your saved tasks.");
        }
        return tasks;
    }

    private static void saveTasks(List<Task> tasks) {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(encodeTask(task));
        }
        try {
            Files.createDirectories(STORAGE_PATH.getParent());
            Files.write(STORAGE_PATH, lines);
        } catch (IOException e) {
            System.out.println("I could not save your tasks.");
        }
    }

    /**
     * Converts a task into one line of the save file, e.g. "{@code D|1|return book|Sunday}".
     * The fields are: type, done flag (1 or 0), description, then any type-specific times.
     */
    private static String encodeTask(Task task) {
        String commonFields = (task.isDone ? "1" : "0") + "|" + task.description;
        if (task instanceof Deadline deadline) {
            return "D|" + commonFields + "|" + deadline.by;
        } else if (task instanceof Event event) {
            return "E|" + commonFields + "|" + event.from + "|" + event.to;
        }
        return "T|" + commonFields;
    }

    /**
     * Converts one line of the save file back into a task.
     *
     * @return the task, or null if the line is malformed and should be skipped.
     */
    private static Task decodeTask(String line) {
        String[] fields = line.split("\\|", -1);
        if (fields.length < 3) {
            return null;
        }
        Task task = createTask(fields);
        if (task != null && fields[1].equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    private static Task createTask(String[] fields) {
        switch (fields[0]) {
        case "T":
            return new Todo(fields[2]);
        case "D":
            return fields.length < 4 ? null : new Deadline(fields[2], fields[3]);
        case "E":
            return fields.length < 5 ? null : new Event(fields[2], fields[3], fields[4]);
        default:
            return null;
        }
    }
}
