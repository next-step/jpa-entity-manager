package persistence.entity.exception;

public class EntityExistsException extends IllegalStateException{
    public EntityExistsException() {
        super("이미 존재하는 Entity입니다.");
    }
}
