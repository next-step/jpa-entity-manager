package persistence.entity;

import persistence.exception.NoRowSnapshotException;

import java.util.HashMap;
import java.util.Map;

public class StatefulPersistenceContext implements PersistenceContext {
    private final Map<EntityKey, Object> entitiesByKey = new HashMap<>();
    private final Map<EntityKey, Object> entitySnapshotsByKey = new HashMap<>();

    @Override
    public boolean hasEntity(EntityKey key) {
        return getEntity(key) != null;
    }

    @Override
    public <T> T getEntity(EntityKey<T> key) {
        return (T) entitiesByKey.get(key);
    }

    @Override
    public void addEntity(Object entity) {
        entitiesByKey.put(new EntityKey(entity), entity);
    }

    @Override
    public void removeEntity(Object entity) {
        entitiesByKey.remove(new EntityKey(entity));
    }

    @Override
    public <T> T getDatabaseSnapshot(EntityKey<T> key, T entity) {
        entitySnapshotsByKey.put(key, entity);
        return entity;
    }

    @Override
    public <T> T getCachedDatabaseSnapshot(EntityKey<T> key) {
        T snapshot = (T) entitySnapshotsByKey.get(key);
        if (snapshot == null) {
            throw new NoRowSnapshotException();
        }
        return snapshot;
    }
}
