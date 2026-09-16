import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Shaheer {
    /** Horizontal line printed above and below every block of chatbot output. */
    private static final String DIVIDER = "____________________________________________________________";

    private static void printAddedMessage(Task task, List<Task> tasks) {
        System.out.println("Got it. I've added this task:\n  " + task + "\nNow you have " + tasks.size() + " tasks in the list.");
    }

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

    /**
     * Builds a Todo from the text after the "todo" command word.
     *
     * @throws ShaheerException if the description is blank.
     */
    private static Todo parseTodo(String args) throws ShaheerException {
        if (args.trim().isEmpty()) {
            throw new ShaheerException("The description of a todo cannot be empty.");
        }
        return new Todo(args.trim());
    }

    /**
     * Builds a Deadline from the text after the "deadline" command word,
     * which must be of the form "<description> /by <when>".
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
     * Builds an Event from the text after the "event" command word, which
     * must be of the form "<description> /from <start> /to <end>".
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

    /**
     * Parses the argument following "mark"/"unmark" and returns the matching
     * task.
     *
     * @throws ShaheerException if the number is missing, not a number, or out of range.
     */
    private static Task getTaskByNumber(List<Task> tasks, String[] commandParts) throws ShaheerException {
        if (commandParts.length < 2) {
            throw new ShaheerException("Please specify the task number, e.g.: mark 2");
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(commandParts[1]);
        } catch (NumberFormatException e) {
            throw new ShaheerException("The task number must be a whole number, e.g.: mark 2");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new ShaheerException("There is no task number " + taskNumber + " in your list.");
        }
        return tasks.get(taskNumber - 1);
    }

    /** Removes the task selected by its one-based list number. */
    private static Task deleteTaskByNumber(List<Task> tasks, String[] commandParts) throws ShaheerException {
        if (commandParts.length < 2) {
            throw new ShaheerException("Please specify the task number, e.g.: delete 2");
        }
        int taskNumber;
        try {
            taskNumber = Integer.parseInt(commandParts[1]);
        } catch (NumberFormatException e) {
            throw new ShaheerException("The task number must be a whole number, e.g.: delete 2");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new ShaheerException("There is no task number " + taskNumber + " in your list.");
        }
        return tasks.remove(taskNumber - 1);
    }

    public static void main(String[] args) {
        printWelcome();
        List<Task> tasks = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            String trimmed = command.trim();
            System.out.println(DIVIDER);
            try {
                if (trimmed.equalsIgnoreCase("bye")) {
                    System.out.println("Bye. Hope to see you back!");
                    System.out.println(DIVIDER);
                    break;
                } else if (trimmed.equalsIgnoreCase("list")) {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < tasks.size(); i++) {
                        System.out.println((i + 1) + "." + tasks.get(i));
                    }
                } else if (trimmed.equalsIgnoreCase("mark") || trimmed.toLowerCase().startsWith("mark ")) {
                    Task task = getTaskByNumber(tasks, trimmed.split(" "));
                    task.markAsDone();
                    System.out.println("Nice! I've marked this task as done:");
                    System.out.println("  " + task);
                } else if (trimmed.equalsIgnoreCase("unmark") || trimmed.toLowerCase().startsWith("unmark ")) {
                    Task task = getTaskByNumber(tasks, trimmed.split(" "));
                    task.markAsNotDone();
                    System.out.println("OK, I've marked this task as not done yet:");
                    System.out.println("  " + task);
                } else if (trimmed.equalsIgnoreCase("delete") || trimmed.toLowerCase().startsWith("delete ")) {
                    Task task = deleteTaskByNumber(tasks, trimmed.split(" "));
                    System.out.println("Noted. I've removed this task:");
                    System.out.println("  " + task);
                    System.out.println("Now you have " + tasks.size() + " tasks in the list.");
                } else if (trimmed.equalsIgnoreCase("todo") || trimmed.toLowerCase().startsWith("todo ")) {
                    String[] parts = trimmed.split(" ", 2);
                    Todo todoTask = parseTodo(parts.length < 2 ? "" : parts[1]);
                    tasks.add(todoTask);
                    printAddedMessage(todoTask, tasks);
                } else if (trimmed.equalsIgnoreCase("deadline") || trimmed.toLowerCase().startsWith("deadline ")) {
                    String[] parts = trimmed.split(" ", 2);
                    Deadline deadlineTask = parseDeadline(parts.length < 2 ? "" : parts[1]);
                    tasks.add(deadlineTask);
                    printAddedMessage(deadlineTask, tasks);
                } else if (trimmed.equalsIgnoreCase("event") || trimmed.toLowerCase().startsWith("event ")) {
                    String[] parts = trimmed.split(" ", 2);
                    Event eventTask = parseEvent(parts.length < 2 ? "" : parts[1]);
                    tasks.add(eventTask);
                    printAddedMessage(eventTask, tasks);
                } else {
                    throw new ShaheerException("I'm sorry, but I don't know what that means");
                }
            } catch (ShaheerException e) {
                System.out.println("Hmmmm! " + e.getMessage());
            }
            System.out.println(DIVIDER);
        }
    }
}