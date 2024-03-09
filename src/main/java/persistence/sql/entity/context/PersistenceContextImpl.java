package persistence.sql.entity.context;

import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.PrimaryDomainType;

public class PersistenceContextImpl implements PersistenceContext {

    private final FirstLevelCache firstLevelCache;
    private final Snapshot snapshot;

    public PersistenceContextImpl() {
        this.firstLevelCache = new FirstLevelCache();
        this.snapshot = new Snapshot();
    }

    @Override
    public <T> T getEntity(final Class<T> clazz, final Object id) {
        final EntityKey entityKey = new EntityKey(clazz.getSimpleName(), id);

        return (T) firstLevelCache.get(entityKey);
    }

    @Override
    public void addEntity(final Object entity, Object id) {
        EntityKey entityKey = new EntityKey(entity.getClass().getSimpleName(), id);

        firstLevelCache.put(entityKey, entity);
        snapshot.put(entityKey, entity);
    }

    @Override
    public void removeEntity(final Object entity) {
        PrimaryDomainType pkDomainTypes = EntityMappingTable.of(entity.getClass(), entity)
                .getPkDomainTypes();
        EntityKey entityKey = new EntityKey(entity.getClass().getSimpleName(), pkDomainTypes.getValue());

        firstLevelCache.remove(entityKey);
        snapshot.remove(entityKey);
    }

    @Override
    public void removeAll() {
        firstLevelCache.clear();
        snapshot.clear();
    }

    @Override
    public <T> T getDatabaseSnapshot(Class<?> clazz, Object id) {
        final EntityKey entityKey = new EntityKey(clazz.getSimpleName(), id);

        return (T) snapshot.get(entityKey);
    }
}
