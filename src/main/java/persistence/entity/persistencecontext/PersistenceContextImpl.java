package persistence.entity.persistencecontext;

import jdbc.JdbcTemplate;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.EntityPersister;
import persistence.sql.ddl.PrimaryKeyClause;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityKey, Object> entityCache;
    private final Map<EntityKey, Object> snapshot;
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;

    public PersistenceContextImpl(JdbcTemplate jdbcTemplate) {
        this.entityCache = new HashMap<>();
        this.snapshot = new HashMap<>();
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
        entityCache.put(new EntityKey(clazz, id), searchedEntity.get());
        snapshot.put(new EntityKey(clazz, id), searchedEntity.get());
        return searchedEntity;
    }

    private Object getCachedEntity(Class<?> clazz, Long id) {
        var key = new EntityKey(clazz, id);
        var cachedEntity = entityCache.get(key);
        if (cachedEntity == null) {
            return null;
        }
        return entityCache.get(key);
    }

    @Override
    public Object addEntity(Object entity) {
        var insertedEntity = entityPersister.insert(entity);

        var key = getEntityKey(insertedEntity);

        entityCache.put(key, insertedEntity);
        snapshot.put(key, insertedEntity);

        return insertedEntity;
    }


    @Override
    public Object updateEntity(Object entity, Long id) {
        var updatedEntity = entityPersister.update(entity, id);

        var key = getEntityKey(updatedEntity);
        entityCache.put(key, updatedEntity);
        snapshot.put(key, updatedEntity);

        return updatedEntity;
    }

    @Override
    public void removeEntity(Object entity) {
        entityPersister.delete(entity);
        var key = getEntityKey(entity);

        entityCache.remove(key);
        snapshot.remove(key);
    }

    private EntityKey getEntityKey(Object entity) {
        return new EntityKey(entity.getClass(), PrimaryKeyClause.primaryKeyValue(entity));
    }

    private EntityKey getEntityKey(Long id, Object entity) {
        return new EntityKey(entity.getClass(), id);
    }

    @Override
    public Optional<Object> getDatabaseSnapshot(Object entity, Long id) {
        return Optional.of(snapshot.get(getEntityKey(id, entity)));
    }
}
