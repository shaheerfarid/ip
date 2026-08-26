import java.util.Scanner;

public class Shaheer {
    public static void main(String[] args) {
        String banner = " ____  _   _    _    _   _ _____ _____ ____  \n"
            + "/ ___|| | | |  / \\  | | | | ____| ____|  _ \\ \n"
            + "\\___ \\| |_| | / _ \\ | |_| |  _| |  _| | |_) |\n"
            + " ___) |  _  |/ ___ \\|  _  | |___| |___|  _ < \n"
            + "|____/|_| |_/_/   \\_\\_| |_|_____|_____|_| \\_\\\n";
        System.out.println("____________________________________________________________");
        System.out.println(banner);
        System.out.println("Hello, I am Shaheer.\nWhat can I do for you?");
        System.out.println("____________________________________________________________");

        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println("____________________________________________________________");
            if (command.trim().equalsIgnoreCase("bye")) {
                System.out.println("Bye. Hope to see you back!");
                System.out.println("____________________________________________________________");
                break;
            }
            System.out.println(command);
            System.out.println("____________________________________________________________");
        }
    }
}