package persistence.entity.persistencecontext;

import java.util.Optional;

public class PersistenceContextImpl implements PersistenceContext {

    private final EntityCache entityCache;
    private final Snapshot snapshot;

    public PersistenceContextImpl() {
        this.entityCache = new EntityCache();
        this.snapshot = new Snapshot();
    }

    @Override
    public <T> Optional<T> getEntity(Class<T> clazz, Long id) {
        if (id  == null) {
           return Optional.empty();
        }
        Optional<Object> cachedEntity = entityCache.get(clazz, id);
        if (cachedEntity.isPresent()) {
            return (Optional<T>) cachedEntity;
        }
        return Optional.empty();
    }

    @Override
    public <T> T addEntity(T entity) {
        entityCache.put(entity);
        snapshot.put(entity);
        return entity;
    }

    @Override
    public <T> T updateEntity(T entity, Long id) {
        entityCache.put(entity);
        snapshot.put(entity);
        return entity;
    }

    @Override
    public void removeEntity(Object entity) {
        entityCache.remove(entity);
        snapshot.remove(entity);
    }

    @Override
    public Optional<Object> getDatabaseSnapshot(Object entity, Long id) {
        Object result = snapshot.get(entity.getClass(), id);
        return Optional.of(result);
    }
}
