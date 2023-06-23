package persistence.entity;

import java.util.HashMap;

public class StatefulPersistenceContext implements PersistenceContext {

    private final HashMap<EntityKey, Object> entitiesByKey;
    private final HashMap<EntityKey, Object> entitySnapshotsByKey;

    public StatefulPersistenceContext(HashMap<EntityKey, Object> entitiesByKey) {
        this.entitiesByKey = entitiesByKey;
        this.entitySnapshotsByKey = new HashMap<>();
    }

    public StatefulPersistenceContext() {
        this(new HashMap<>());
    }

    @Override
    public Object removeEntity(EntityKey key) {
        return entitiesByKey.remove(key);
    }

    @Override
    public Object getEntity(EntityKey key) {
        return entitiesByKey.get(key);
    }

    @Override
    public boolean containsEntity(EntityKey key) {
        return entitiesByKey.containsKey(key);
    }

    @Override
    public void addEntity(EntityKey key, Object entity) {
        entitiesByKey.put(key, entity);
    }

    @Override
    public Object getDatabaseSnapshot(Long id, Object entity) {
        EntityKey entityKey = EntityKey.of(id, entity.getClass().getSimpleName());
        Object cached = getCached(entityKey);
        if (cached != null) {
            return cached;
        }

        entitySnapshotsByKey.put(entityKey, entity);
        return entity;
    }

    @Override
    public Object getCachedDatabaseSnapshot(EntityKey entityKey) {
        Object snapshot = getCached(entityKey);

        if (snapshot == null) {
            throw new IllegalArgumentException("not found for " + entityKey);
        }

        return snapshot;
    }

    private Object getCached(EntityKey entityKey) {
        return entitySnapshotsByKey == null ? null : entitySnapshotsByKey.get(entityKey);
    }
}
