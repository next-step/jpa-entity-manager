package persistence.entity;

public class DefaultEntityEntryFactory implements EntityEntryFactory {
    public EntityEntry createEntityEntry(EntityPersister entityPersister, EntityLoader entityLoader, Status status) {
        return new EntityEntryImpl(entityPersister, entityLoader, status);
    }
}
