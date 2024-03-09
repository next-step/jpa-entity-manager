package persistence.sql.entity.context;

import persistence.sql.entity.EntityMappingTable;
import persistence.sql.entity.model.PrimaryDomainType;

public class PersistenceContextImpl implements PersistenceContext {

    private final FirstLevelCache firstLevelCache;
    private final Snapshot snapshot;
    private final EntityEntryContext entityEntryContext;

    public PersistenceContextImpl() {
        this.firstLevelCache = new FirstLevelCache();
        this.snapshot = new Snapshot();
        this.entityEntryContext = new EntityEntryContext();
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
        entityEntryContext.managed(entityKey);
    }

    @Override
    public void removeEntity(final Object entity) {
        PrimaryDomainType pkDomainTypes = EntityMappingTable.of(entity.getClass(), entity)
                .getPkDomainTypes();
        EntityKey entityKey = new EntityKey(entity.getClass().getSimpleName(), pkDomainTypes.getValue());

        entityEntryContext.delete(entityKey);
    }

    @Override
    public void goneEntity(Object entity) {
        PrimaryDomainType pkDomainTypes = EntityMappingTable.of(entity.getClass(), entity)
                .getPkDomainTypes();
        EntityKey entityKey = new EntityKey(entity.getClass().getSimpleName(), pkDomainTypes.getValue());

        entityEntryContext.gone(entityKey);
    }

    @Override
    public void saving(Object entity) {
        PrimaryDomainType pkDomainTypes = EntityMappingTable.of(entity.getClass(), entity)
                .getPkDomainTypes();
        EntityKey entityKey = new EntityKey(entity.getClass().getSimpleName(), pkDomainTypes.getValue());

        entityEntryContext.saving(entityKey);
    }

    @Override
    public void loading(Object entity, Object id) {
        EntityKey entityKey = new EntityKey(entity.getClass().getSimpleName(), id);
        entityEntryContext.loading(entityKey);
    }

    @Override
    public void readOnly(Object entity, Object id) {
        EntityKey entityKey = new EntityKey(entity.getClass().getSimpleName(), id);
        entityEntryContext.readOnly(entityKey);
    }

    @Override
    public void removeAll() {
        firstLevelCache.clear();
        snapshot.clear();
        entityEntryContext.clear();
    }

    @Override
    public <T> T getDatabaseSnapshot(Class<?> clazz, Object id) {
        final EntityKey entityKey = new EntityKey(clazz.getSimpleName(), id);

        return (T) snapshot.get(entityKey);
    }

    @Override
    public boolean isGone(Class<?> clazz, Object id) {
        EntityKey entityKey = new EntityKey(clazz.getSimpleName(), id);

        EntityEntry entityEntry = entityEntryContext.getEntityEntry(entityKey);
        if(entityEntry == null) {
            return false;
        }

        return entityEntryContext.getEntityEntry(entityKey)
                .isGone();
    }

    @Override
    public boolean isReadOnly(Object entity) {
        PrimaryDomainType pkDomainTypes = EntityMappingTable.of(entity.getClass(), entity)
                .getPkDomainTypes();
        EntityKey entityKey = new EntityKey(entity.getClass().getSimpleName(), pkDomainTypes.getValue());

        EntityEntry entityEntry = entityEntryContext.getEntityEntry(entityKey);
        if(entityEntry == null) {
            return false;
        }
        return entityEntry.isReadOnly();
    }
}
