package persistence.exception;

public class NotFoundIdException extends RuntimeException {

    public NotFoundIdException() {
        super("ID가 존재하지 않습니다.");
    }

    public NotFoundIdException(String message) {
        super(message);
    }
}
