package persistence.exception;


/**
 * 엔티티의 필드가 비어 있을때 발생되는 예외
 * */
public class FieldEmptyException extends IllegalArgumentException {
    private final static String DUALIST_MESSAGE = "필드가 비어있습니다.";

    public FieldEmptyException(String message) {
        super(message);
    }

    public FieldEmptyException() {
        super(DUALIST_MESSAGE);
    }
}
