package exceptions;

public class TerminatedAccountException extends Exception {
    public TerminatedAccountException(String message) {
        super(message);
    }
}
