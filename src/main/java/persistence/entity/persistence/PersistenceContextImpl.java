package persistence.entity.persistence;

import persistence.entity.domain.EntityEntry;
import persistence.entity.domain.EntityKey;
import persistence.entity.domain.EntitySnapshot;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityKey, Object> entities = new HashMap<>();
    private final Map<EntityKey, EntitySnapshot> snapshots = new HashMap<>();
    private final Map<EntityKey, EntityEntry> entityEntries = new HashMap<>();

    @Override
    public <T> T getEntity(Class<T> clazz, Object id) {
        return (T) entities.get(new EntityKey(clazz, id));
    }

    @Override
    public void addEntity(Object id, Object entity) {
        EntityKey entityKey = new EntityKey(entity.getClass(), id);
        entities.put(entityKey, entity);
    }

    @Override
    public void removeEntity(Object entity) {
        EntityKey entityKey = new EntityKey(entity);
        entities.remove(entityKey);
    }

    @Override
    public EntitySnapshot getDatabaseSnapshot(Object id, Object entity) {
        EntityKey entityKey = new EntityKey(entity.getClass(), id);
        return snapshots.computeIfAbsent(entityKey, key -> new EntitySnapshot(entity));
    }

    @Override
    public EntitySnapshot getCachedDatabaseSnapshot(Object id, Object entity) {
        EntityKey entityKey = new EntityKey(entity.getClass(), id);
        return snapshots.get(entityKey);
    }

    @Override
    public void addEntityEntry(Object entity, EntityEntry entityEntry) {
        entityEntries.put(new EntityKey(entity), entityEntry);
    }

    @Override
    public EntityEntry getEntityEntry(Object entity) {
        return entityEntries.get(new EntityKey(entity));
    }

}
