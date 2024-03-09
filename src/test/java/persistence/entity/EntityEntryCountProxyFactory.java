package persistence.entity;

public class EntityEntryCountProxyFactory implements EntityEntryFactory {
    @Override
    public EntityEntry createEntityEntry(EntityPersister entityPersister, EntityLoader entityLoader, Status status) {
        return new EntityEntryCountProxy(new EntityEntryImpl(entityPersister, entityLoader, status));
    }
}
