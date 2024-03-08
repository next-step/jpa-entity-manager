package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<EntityKey, Object> entitiesByKey = new HashMap<>();
    private final Map<EntityKey, Snapshot> snapshotsByKey = new HashMap<>();
    @Override
    public Object getEntity(EntityKey id) {
        return entitiesByKey.get(id);
    }

    @Override
    public void addEntity(EntityKey id, Object entity) {
        entitiesByKey.put(id, entity);
        snapshotsByKey.put(id, new Snapshot(entity));
    }

    @Override
    public void removeEntity(Object entity) {
        EntityKey key = EntityKey.fromEntity(entity);
        entitiesByKey.remove(key);
        snapshotsByKey.remove(key);
    }

    @Override
    public boolean isDirty(EntityKey id, Object entity) {
        Snapshot snapshot = snapshotsByKey.get(id);
        return !snapshot.equals(new Snapshot(entity));
    }
}
