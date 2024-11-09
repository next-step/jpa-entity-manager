package persistence.exception;

public class NotSameException extends RuntimeException {

    private static final String MESSAGE_PREFIX = "Not same as ";

    public NotSameException(String message) {
        super(MESSAGE_PREFIX + message);
    }

    public NotSameException(String message, Throwable cause) {
        super(MESSAGE_PREFIX + message, cause);
    }

}
