package persistence.entity;

public class EntityAlreadyExistsException extends RuntimeException {
    public EntityAlreadyExistsException(EntityKey entityKey) {
        super(entityKey + " already exists");
    }
}
