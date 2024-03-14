package persistence.entity.exception;

public class UnableToChangeIdException extends IllegalStateException{
    public UnableToChangeIdException() {
        super("Id를 변경할 수 없습니다.");
    }
}
