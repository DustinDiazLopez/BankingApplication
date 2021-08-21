package exceptions;

public class LoginFailedException extends Exception {
    public LoginFailedException(final String message) {
        super(message);
    }
}
