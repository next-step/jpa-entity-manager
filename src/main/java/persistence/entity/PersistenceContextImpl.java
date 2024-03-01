package persistence.entity;

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
        Object cachedEntity = firstLevelCache.get(entityCacheKey);
        return clazz.cast(cachedEntity);
    }

    @Override
    public void addEntity(Object entity) {
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity);
        firstLevelCache.put(entityCacheKey, entity);
        snapshotStorage.add(entity);
    }

    @Override
    public void removeEntity(Object entity) {
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity);
        firstLevelCache.remove(entityCacheKey);
        snapshotStorage.remove(entityCacheKey);
    }

    @Override
    public boolean isDirty(Object entity) {
        return snapshotStorage.isDirty(entity);
    }
}
