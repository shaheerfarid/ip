import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

public class Shaheer {
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
        List<String> tasks = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println(printDivider);
            if (command.trim().equalsIgnoreCase("bye")) {
                System.out.println("Bye. Hope to see you back!");
                System.out.println(printDivider);
                break;
            }
            if (command.trim().equalsIgnoreCase("list")) {
                for (int i = 0; i < tasks.size(); i++) {
                    System.out.println((i + 1) + ". " + tasks.get(i));
                }
                System.out.println(printDivider);
                continue;
            }
            // System.out.println(command);
            tasks.add(command);
            System.out.println("added: " + command);
            System.out.println(printDivider);
        }
    }
}