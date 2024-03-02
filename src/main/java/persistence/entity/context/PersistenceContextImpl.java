package persistence.entity.context;

public class PersistenceContextImpl implements PersistenceContext {
    private final FirstLevelCache firstLevelCache;
    private final EntityEntries entityEntries;

    public PersistenceContextImpl() {
        firstLevelCache = new FirstLevelCache();
        entityEntries = new EntityEntries();
    }

    @Override
    public Object getEntity(Class<?> clazz, Long id) {
        EntityKey entityKey = EntityKey.of(clazz, id);

        if (!entityEntries.canGet(entityKey)) return null;
        return firstLevelCache.get(entityKey);
    }

    @Override
    public void addEntity(Object entity) {
        EntityKey entityKey = EntityKey.of(entity);

        if (entityEntries.canAdd(entityKey)) {
            firstLevelCache.store(entityKey, entity);
            entityEntries.manage(entityKey);
        }
    }

    @Override
    public boolean isRemoved(Object entity) {
        EntityKey entityKey = EntityKey.of(entity);

        return entityEntries.isRemoved(entityKey);
    }

    @Override
    public void removeEntity(Object entity) {
        EntityKey entityKey = EntityKey.of(entity);

        if (entityEntries.canRemove(entityKey)) {
            entityEntries.setAsDeleted(entityKey);
            firstLevelCache.delete(entityKey);
            entityEntries.gone(entityKey);
        }
    }
}
