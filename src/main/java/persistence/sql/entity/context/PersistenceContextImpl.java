package persistence.sql.entity.context;

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
        if(id == null) {
            return null;
        }

        final EntityKey entityKey = new EntityKey(clazz.getSimpleName(), id);

        return (T) firstLevelCache.get(entityKey);
    }

    @Override
    public void addEntity(final Object entity, Object id) {
        EntityKey entityKey = new EntityKey(entity.getClass().getSimpleName(), id);

        firstLevelCache.put(entityKey, entity);
        snapshot.put(entityKey, entity);
        entityEntryContext.managed(entity);
    }

    @Override
    public void removeEntity(final Object entity) {
        entityEntryContext.delete(entity);
    }

    @Override
    public void goneEntity(Object entity) {
        entityEntryContext.gone(entity);
    }

    @Override
    public void saving(Object entity) {
        entityEntryContext.saving(entity);
    }

    @Override
    public void loading(Object entity, Object id) {
        entityEntryContext.loading(entity);
    }

    @Override
    public void readOnly(Object entity, Object id) {
        entityEntryContext.readOnly(entity);
    }

    @Override
    public void removeAll() {
        firstLevelCache.clear();
        snapshot.clear();
        entityEntryContext.clear();
    }

    @Override
    public <T> T getDatabaseSnapshot(Class<?> clazz, Object id) {
        if(id == null) {
            return null;
        }

        final EntityKey entityKey = new EntityKey(clazz.getSimpleName(), id);

        return (T) snapshot.get(entityKey);
    }

    @Override
    public boolean isGone(Object entity) {
        EntityEntry entityEntry = entityEntryContext.getEntityEntry(entity);

        if(entityEntry == null) {
            return false;
        }

        return entityEntryContext.getEntityEntry(entity)
                .isGone();
    }

    @Override
    public boolean isReadOnly(Object entity) {
        EntityEntry entityEntry = entityEntryContext.getEntityEntry(entity);
        if(entityEntry == null) {
            return false;
        }
        return entityEntry.isReadOnly();
    }
}
