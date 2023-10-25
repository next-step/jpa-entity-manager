package persistence.exception;

public class NotFoundValueException extends RuntimeException {

    public NotFoundValueException() {
        super("유효한 값이 존재하지 않습니다");
    }

    public NotFoundValueException(String message) {
        super(message);
    }
}
