package persistence.entity.exception;

public class UpdatedEntityNotExistsException extends RuntimeException{
    public UpdatedEntityNotExistsException() {
        super("업데이트 된 엔티티는 하나 이상 존재합니다.");
    }
}
