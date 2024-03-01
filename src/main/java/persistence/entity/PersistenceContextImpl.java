package persistence.entity;

import persistence.sql.domain.DatabaseTable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityCacheKey, Object> firstLevelCache = new ConcurrentHashMap<>();

    private final SnapshotStorage snapshotStorage;

    public PersistenceContextImpl(SnapshotStorage snapshotStorage) {
        this.snapshotStorage = snapshotStorage;
    }

    @Override
    public <T> T getEntity(Class<T> clazz, Object id) {
        EntityCacheKey entityCacheKey = new EntityCacheKey(clazz, id);
        return (T) firstLevelCache.get(entityCacheKey);
    }

    @Override
    public void addEntity(Object entity) {
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity);
        firstLevelCache.put(entityCacheKey, entity);
        snapshotStorage.add(entity);
    }

    @Override
    public void removeEntity(Object entity) {
        String id = new DatabaseTable(entity).getPrimaryColumn().getColumnValue();
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity.getClass(), id);
        firstLevelCache.remove(entityCacheKey);
        snapshotStorage.remove(entityCacheKey);
    }

    @Override
    public void updateCache(Object entity) {
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity);
        if (firstLevelCache.containsKey(entityCacheKey)){
            firstLevelCache.put(entityCacheKey, entity);
        }
    }

    @Override
    public boolean isNotDirty(Object entity, Object id) {
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity.getClass(), id);
        return !snapshotStorage.isDirty(entityCacheKey, entity);
    }
}
