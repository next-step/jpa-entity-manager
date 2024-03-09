package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<EntityKey, Object> cache;
    private final Map<EntityKey, Object> snapshot;
    private final Map<EntityKey, EntityEntry> entry;

    public SimplePersistenceContext() {
        this.cache = new HashMap<>();
        this.snapshot = new HashMap<>();
        this.entry = new HashMap<>();
    }

    @Override
    public <T> T getEntity(Class<T> clazz, EntityId id) {
        EntityKey key = new EntityKey(clazz, id);
        return (T) cache.get(key);
    }

    @Override
    public void addEntity(EntityId id, Object entity) {
        EntityBinder entityBinder = new EntityBinder(entity);
        entityBinder.bindEntityId(id);

        EntityKey key = createEntityKey(entity, id);
        cache.put(key, entity);
        snapshot.put(key, entity);

        EntityEntry entry = new EntityEntry(Status.MANAGED);
        this.entry.put(key, entry);
    }

    @Override
    public void removeEntity(Object entity) {
        EntityKey key = createEntityKey(entity);
        cache.remove(key);

        EntityEntry entry = new EntityEntry(Status.DELETED);
        this.entry.put(key, entry);
    }

    @Override
    public boolean isCached(Object entity) {
        EntityKey key = createEntityKey(entity);
        EntityEntry entry = this.entry.get(key);
        if (entry == null) {
            return false;
        }

        Status status = entry.status();
        return status == Status.MANAGED;
    }

    @Override
    public Object getDatabaseSnapshot(EntityId id, Object entity) {
        EntityKey key = createEntityKey(entity, id);
        return snapshot.get(key);
    }

    @Override
    public EntityEntry getEntityEntry(Object entity) {
        EntityKey key = createEntityKey(entity);
        return entry.get(key);
    }

    @Override
    public EntityEntry getEntityEntry(Class<?> clazz, EntityId id) {
        EntityKey key = new EntityKey(clazz, id);
        return entry.get(key);
    }

    private EntityKey createEntityKey(Object entity, EntityId id) {
        Class<?> clazz = entity.getClass();
        return new EntityKey(clazz, id);
    }

    private EntityKey createEntityKey(Object entity) {
        EntityBinder entityBinder = new EntityBinder(entity);

        Class<?> clazz = entity.getClass();
        EntityId id = entityBinder.getEntityId();

        return new EntityKey(clazz, id);
    }
}
