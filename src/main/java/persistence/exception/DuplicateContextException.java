package persistence.exception;

public class DuplicateContextException extends RuntimeException {
    public DuplicateContextException() {
        super("이미 중복된 데이터가 존재합니다.");
    }

    public DuplicateContextException(String message) {
        super(message);
    }
}
