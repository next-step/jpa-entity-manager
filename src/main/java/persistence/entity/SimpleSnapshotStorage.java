package persistence.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleSnapshotStorage implements SnapshotStorage {

    private final Map<EntityCacheKey, Snapshot> snapshotStorage = new ConcurrentHashMap<>();

    @Override
    public void add(Object entity) {
        Snapshot snapshot = new Snapshot(entity);
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity);
        snapshotStorage.put(entityCacheKey, snapshot);
    }

    @Override
    public void remove(EntityCacheKey entityCacheKey) {
        snapshotStorage.remove(entityCacheKey);
    }

    @Override
    public boolean isDirty(Object entity) {
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity);

        Snapshot before = snapshotStorage.get(entityCacheKey);
        if (before == null) {
            return false;
        }
        Snapshot after = new Snapshot(entity);

        return !before.equals(after);
    }


}
