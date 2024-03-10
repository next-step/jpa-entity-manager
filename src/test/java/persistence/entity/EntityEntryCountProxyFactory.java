package persistence.entity;

public class EntityEntryCountProxyFactory implements EntityEntryFactory {
    @Override
    public EntityEntry createEntityEntry( Status status) {
        return new EntityEntryCountProxy(new EntityEntryImpl(status));
    }
}
