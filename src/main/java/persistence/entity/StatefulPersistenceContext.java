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
        removeCache(key);
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
        addCache(entity, key);
    }

    @Override
    public Object getDatabaseSnapshot(Object id, Object entity) {
        EntityKey entityKey = EntityKey.of(id, entity.getClass().getSimpleName());
        Object cached = getCached(entityKey);
        if (cached != null) {
            return cached;
        }

        addCache(entity, entityKey);
        return entity;
    }

    @Override
    public Object getCachedDatabaseSnapshot(EntityKey key) {
        Object snapshot = getCached(key);

        if (snapshot == null) {
            return NO_ROW;
        }

        return snapshot;
    }

    private void removeCache(EntityKey key) {
        entitySnapshotsByKey.remove(key);
    }

    private void addCache(Object entity, EntityKey key) {
        entitySnapshotsByKey.put(key, entity);
    }

    public Object getCached(EntityKey key) {
        return entitySnapshotsByKey == null ? null : entitySnapshotsByKey.get(key);
    }
}
