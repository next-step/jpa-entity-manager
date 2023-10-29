package persistence.exception;

public class InvalidContextException extends RuntimeException {
    public InvalidContextException() {
        super("존재하지 않는 데이터입니다.");
    }

    public InvalidContextException(String message) {
        super(message);
    }
}
