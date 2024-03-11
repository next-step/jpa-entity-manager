package persistence.entity.persistencecontext;

import jdbc.JdbcTemplate;
import persistence.entity.EntityLoader;
import persistence.entity.EntityPersister;
import persistence.sql.ddl.PrimaryKeyClause;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityCacheKey, Object> entityCache;
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;

    public PersistenceContextImpl(JdbcTemplate jdbcTemplate) {
        this.entityCache = new HashMap<>();
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
        entityCache.put(new EntityCacheKey(clazz, id), searchedEntity.get());
        return searchedEntity;
    }

    private Object getCachedEntity(Class<?> clazz, Long id) {
        var key = new EntityCacheKey(clazz, id);
        var cachedEntity = entityCache.get(key);
        if (cachedEntity == null) {
            return null;
        }
        return entityCache.get(key);
    }

    @Override
    public void addEntity(Object entity) {
        entityPersister.insert(entity);
    }

    @Override
    public void removeEntity(Object entity) {
        entityPersister.delete(entity);
        var key = getEntityCacheKey(entity);
        entityCache.remove(key);
    }

    private EntityCacheKey getEntityCacheKey(Object entity) {
        return new EntityCacheKey(entity.getClass(), PrimaryKeyClause.primaryKeyValue(entity));
    }
}
