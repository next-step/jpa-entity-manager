package persistence.entity.persistencecontext;

import java.util.Optional;

import static persistence.sql.ddl.clause.primkarykey.PrimaryKeyValue.getPrimaryKeyValue;

public class PersistenceContextImpl implements PersistenceContext {

    private final EntityCache entityCache;
    private final Snapshot snapshot;

    public PersistenceContextImpl() {
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
    public <T> T addEntity(T entity) {
        Class<?> clazz = entity.getClass();
        Long id = getPrimaryKeyValue(entity);
        entityCache.put(entity, new EntityKey(clazz, id));
        return entity;
    }

    @Override
    public <T> T    updateEntity(T entity, Long id) {
        EntityKey key = new EntityKey(entity.getClass(), getPrimaryKeyValue(entity));
        entityCache.put(entity, key);
        snapshot.put(entity, key);
        return entity;
    }

    @Override
    public void removeEntity(Object entity) {
        Class<?> clazz = entity.getClass();
        Long id = getPrimaryKeyValue(entity);
        EntityKey entityKey = new EntityKey(clazz, id);
        entityCache.remove(entityKey);
        snapshot.remove(entityKey);
    }

    @Override
    public <T> T getDatabaseSnapshot(T entity, Long id) {
        EntityKey key = new EntityKey(entity.getClass(), id);
        return snapshot.get(key);
    }
}
