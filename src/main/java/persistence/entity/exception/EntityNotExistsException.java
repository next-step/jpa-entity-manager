package persistence.entity.exception;

public class EntityNotExistsException extends IllegalStateException{
    public EntityNotExistsException() {
        super("존재하지 않는 Entity입니다..");
    }
}
