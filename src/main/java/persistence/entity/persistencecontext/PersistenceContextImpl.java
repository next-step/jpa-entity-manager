package persistence.entity.persistencecontext;

import jdbc.JdbcTemplate;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.EntityPersister;

import java.util.Optional;

public class PersistenceContextImpl implements PersistenceContext {

    private final EntityCache entityCache;
    private final Snapshot snapshot;
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;

    public PersistenceContextImpl(JdbcTemplate jdbcTemplate) {
        this.entityCache = new EntityCache();
        this.snapshot = new Snapshot();
        this.entityLoader = new EntityLoader(jdbcTemplate);
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }

    @Override
    public <T> Optional<T> getEntity(Class<T> clazz, Long id) {
        var cachedEntity = getCachedEntity(clazz, id);
        if (cachedEntity != null) {
            return Optional.of((T) cachedEntity);
        }

        var searchedEntity = entityLoader.find(clazz, id);
        if (searchedEntity.isEmpty()) {
            return Optional.empty();
        }
        entityCache.put(id, searchedEntity.get());
        snapshot.put(id, searchedEntity.get());
        return searchedEntity;
    }

    private Object getCachedEntity(Class<?> clazz, Long id) {
        return entityCache.get(clazz, id);
    }

    @Override
    public Object addEntity(Object entity) {
        var insertedEntity = entityPersister.insert(entity);
        entityCache.put(insertedEntity);
        snapshot.put(insertedEntity);
        return insertedEntity;
    }


    @Override
    public Object updateEntity(Object entity, Long id) {
        var updatedEntity = entityPersister.update(entity, id);
        entityCache.put(id, updatedEntity);
        snapshot.put(id, updatedEntity);
        return updatedEntity;
    }

    @Override
    public void removeEntity(Object entity) {
        entityPersister.delete(entity);
        entityCache.remove(entity);
        snapshot.remove(entity);
    }

    @Override
    public Optional<Object> getDatabaseSnapshot(Object entity, Long id) {
        var result = snapshot.get(entity.getClass(), id);
        return Optional.of(result);
    }
}
