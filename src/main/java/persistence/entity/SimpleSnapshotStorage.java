package persistence.entity;

import persistence.sql.domain.DatabaseTable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleSnapshotStorage implements SnapshotStorage{

    private final Map<EntityCacheKey, DatabaseTable> snapshotStorage = new ConcurrentHashMap<>();

    @Override
    public void add(Object entity) {
        DatabaseTable snapshot = new DatabaseTable(entity);
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

        DatabaseTable before = snapshotStorage.get(entityCacheKey);
        if (before == null){
            return false;
        }
        DatabaseTable after = new DatabaseTable(entity);

        return !before.equals(after);
    }


}
