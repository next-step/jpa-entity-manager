package persistence;

import java.util.HashMap;

public class DefaultPersistenceContext implements PersistenceContext {

    private HashMap<Long, Object> entitiesByKey;
    private HashMap<Long, Object> entitySnapshotsByKey;

    @Override
    public Object getEntity(Long id) {
        return entitiesByKey == null ? null : entitiesByKey.get(id);
    }

    @Override
    public void addEntity(Long id, Object entity) {
        if (entitiesByKey == null) {
            entitiesByKey = new HashMap<>();
        }

        entitiesByKey.put(id, entity);
    }

    @Override
    public Object removeEntity(Long id) {
        if (entitiesByKey != null) {
            return entitiesByKey.remove(id);
        }

        return null;
    }

    @Override
    public Object getCachedDatabaseSnapshot(Long id, Object entity) {
        Object snapshot = entitySnapshotsByKey == null ? null : entitySnapshotsByKey.get(id);

        if (snapshot != null) {
            return snapshot;
        }

        if (entitySnapshotsByKey == null) {
            entitySnapshotsByKey = new HashMap<>();
        }

        return entitySnapshotsByKey.putIfAbsent(id, entity);
    }
}
