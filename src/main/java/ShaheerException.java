/**
 * Signals a problem with user input that Shaheer (the chatbot) knows how to
 * explain in a friendly way, e.g. a missing task description or an
 * unrecognised command. Thrown by the parsing helpers in {@link Shaheer}
 * and caught in its main loop, where the message is shown to the user
 * prefixed with "OOPS!!!".
 */
public class ShaheerException extends Exception {
    public ShaheerException(String message) {
        super(message);
    }
}
