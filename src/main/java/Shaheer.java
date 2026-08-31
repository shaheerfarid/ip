import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Shaheer {


    private static void printAddedMessage(Task task, List<Task> tasks) {
        System.out.println("Got it. I've added this task:\n  " + task + "\nNow you have " + tasks.size() + " tasks in the list.");
    }

    public static void main(String[] args) {
        String banner = " ____  _   _    _    _   _ _____ _____ ____  \n"
            + "/ ___|| | | |  / \\  | | | | ____| ____|  _ \\ \n"
            + "\\___ \\| |_| | / _ \\ | |_| |  _| |  _| | |_) |\n"
            + " ___) |  _  |/ ___ \\|  _  | |___| |___|  _ < \n"
            + "|____/|_| |_/_/   \\_\\_| |_|_____|_____|_| \\_\\\n";
        String printDivider = "____________________________________________________________";
        System.out.println(printDivider);
        System.out.println(banner);
        System.out.println("Hello, I am Shaheer.\nWhat can I do for you?");
        System.out.println(printDivider);
        List<Task> tasks = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            String trimmed = command.trim();
            System.out.println(printDivider);
            if (trimmed.equalsIgnoreCase("bye")) {
                System.out.println("Bye. Hope to see you back!");
                System.out.println(printDivider);
                break;
            } else if (trimmed.equalsIgnoreCase("list")) {
                System.out.println("Here are the tasks in your list:");
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println((i + 1) + "." + tasks.get(i));
                }
            } else if (trimmed.toLowerCase().startsWith("mark ")) {
                String[] parts = trimmed.split(" ");
                if (parts.length < 2) {
                    System.out.println("Please specify the task number to mark.");
                } else {
                    try {
                        int taskNumber = Integer.parseInt(parts[1]);
                        if (taskNumber < 1 || taskNumber > tasks.size()) {
                            System.out.println("Invalid task number.");
                        } else {
                            Task task = tasks.get(taskNumber - 1);
                            task.markAsDone();
                            System.out.println("Nice! I've marked this task as done:");
                            System.out.println("  " + task);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please specify a valid task number.");
                    }
                }
            } else if (trimmed.toLowerCase().startsWith("unmark ")) {
                String[] parts = trimmed.split(" ");
                if (parts.length < 2) {
                    System.out.println("Please specify the task number to unmark.");
                } else {
                    try {
                        int taskNumber = Integer.parseInt(parts[1]);
                        if (taskNumber < 1 || taskNumber > tasks.size()) {
                            System.out.println("Invalid task number.");
                        } else {
                            Task task = tasks.get(taskNumber - 1);
                            task.markAsNotDone();
                            System.out.println("OK, I've marked this task as not done yet:");
                            System.out.println("  " + task);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Please specify a valid task number.");
                    }
                }
            } else if (trimmed.toLowerCase().startsWith("todo ")){
                String[] parts = trimmed.split(" ", 2);
                Todo todoTask = new Todo(parts[1]);
                tasks.add(todoTask);
                printAddedMessage(todoTask, tasks);

            } else if (trimmed.toLowerCase().startsWith("deadline ")){
                String[] parts = trimmed.split(" ", 2);
                String[] deadlineParts = parts[1].split(" /by ", 2);
                Deadline deadlineTask = new Deadline(deadlineParts[0], deadlineParts[1]);
                tasks.add(deadlineTask);
                printAddedMessage(deadlineTask, tasks);

            } else if (trimmed.toLowerCase().startsWith("event ")){
                String[] parts = trimmed.split(" ", 2);
                String[] eventParts = parts[1].split(" /from ", 2);
                String[] eventTimeParts = eventParts[1].split(" /to ", 2);
                Event eventTask = new Event(eventParts[0], eventTimeParts[0], eventTimeParts[1]);
                tasks.add(eventTask);
                printAddedMessage(eventTask, tasks);

            } else {
                tasks.add(new Task(command));
                System.out.println("added: " + command);
            }
            System.out.println(printDivider);
        }
    }
}
