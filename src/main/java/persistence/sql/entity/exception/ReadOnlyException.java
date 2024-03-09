package persistence.sql.entity.exception;

public class ReadOnlyException extends RuntimeException{

    private static final String MESSAGE = "읽기전용은 수정 및 삭제가 불가능 합니다.";

    public ReadOnlyException() {
        super(MESSAGE);
    }
}
