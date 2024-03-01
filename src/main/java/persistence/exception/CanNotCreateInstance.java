package persistence.exception;

public class CanNotCreateInstance extends RuntimeException {
    public CanNotCreateInstance(String message, Throwable cause) {
        super(message, cause);
    }
}
