package persistence.sql.entity.exception;

public class RemoveEntityException extends RuntimeException{

    private static final String MESSAGE = "삭제된 엔티티입니다.";

    public RemoveEntityException() {
        super(MESSAGE);
    }
}
