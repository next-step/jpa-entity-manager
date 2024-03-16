package persistence.entity.persistencecontext;

import persistence.PrimaryKey;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityKey, EntityEntry> entityEntries;
    private final EntityCache entityCache;
    private final Snapshot snapshot;

    public PersistenceContextImpl() {
        this.entityEntries = new HashMap<>();
        this.entityCache = new EntityCache();
        this.snapshot = new Snapshot();
    }

    @Override
    public <T> Optional<T> getEntity(Class<T> clazz, Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<Object> cachedEntity = entityCache.get(new EntityKey(clazz, id));
        if (cachedEntity.isPresent()) {
            return (Optional<T>) cachedEntity;
        }
        return Optional.empty();
    }

    @Override
    public <T> T addEntity(T entity, Long id) {
        Class<?> clazz = entity.getClass();
        EntityKey entityKey = new EntityKey(clazz, id);
        entityCache.put(entity, entityKey);
        return entity;
    }

    @Override
    public <T> T updateEntity(T entity, Long id) {
        EntityKey key = new EntityKey(entity.getClass(), id);
        entityCache.put(entity, key);
        snapshot.put(entity, key);
        return entity;
    }

    @Override
    public void removeEntity(Object entity) {
        Class<?> clazz = entity.getClass();
        Long id = new PrimaryKey(entity.getClass()).getPrimaryKeyValue(entity);
        EntityKey entityKey = new EntityKey(clazz, id);
        entityCache.remove(entityKey);
        snapshot.remove(entityKey);
    }

    @Override
    public <T> T getDatabaseSnapshot(T entity, Long id) {
        EntityKey key = new EntityKey(entity.getClass(), id);
        return snapshot.get(key);
    }

    @Override
    public EntityEntry getEntityEntry(Class<?> clazz, Long id) {
        return this.entityEntries.get(new EntityKey(clazz, id));
    }

    @Override
    public <T> void addEntityEntry(Class<?> clazz, Long id) {
        this.entityEntries.put(new EntityKey(clazz, id), new EntityEntry());
    }
}
