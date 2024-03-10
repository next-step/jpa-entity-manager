package persistence.entity;

public class EntityReadOnlyException extends RuntimeException {
    public EntityReadOnlyException(EntityKey entityKey) {
        super("Entity with key " + entityKey + " is read-only");
    }
}
