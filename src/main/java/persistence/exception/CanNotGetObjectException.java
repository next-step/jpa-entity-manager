package persistence.exception;

public class CanNotGetObjectException extends RuntimeException {
    public CanNotGetObjectException(String message, Throwable cause) {
        super(message, cause);
    }
}
