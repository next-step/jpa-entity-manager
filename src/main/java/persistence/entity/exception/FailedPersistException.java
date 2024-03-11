package persistence.entity.exception;

public class FailedPersistException extends IllegalStateException{
    public FailedPersistException() {
        super("entity 식별자(primary key)가 없습니다.");
    }
}
