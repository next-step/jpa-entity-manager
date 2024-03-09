package persistence.entity;

public class EntityNotExistsException extends RuntimeException {
    public EntityNotExistsException(EntityKey entityKey) {
        super(entityKey + " does not exist");
    }
}
