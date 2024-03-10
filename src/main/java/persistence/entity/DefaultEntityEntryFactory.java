package persistence.entity;

public class DefaultEntityEntryFactory implements EntityEntryFactory {
    public EntityEntry createEntityEntry(Status status) {
        return new EntityEntryImpl(status);
    }
}
