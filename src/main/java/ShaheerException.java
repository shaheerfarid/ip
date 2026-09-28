/**
 * Signals a problem that Shaheer (the chatbot) knows how to explain to the
 * user in a friendly way, e.g. a missing task description, an unrecognised
 * command, or a save file that cannot be read or written.
 */
public class ShaheerException extends Exception {
    public ShaheerException(String message) {
        super(message);
    }
}
