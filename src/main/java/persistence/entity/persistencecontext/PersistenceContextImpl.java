package persistence.entity.persistencecontext;

import jdbc.JdbcTemplate;
import persistence.entity.EntityLoader;
import persistence.entity.EntityPersister;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<Long, Object> entityCache;
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;

    public PersistenceContextImpl(JdbcTemplate jdbcTemplate) {
        this.entityCache = new HashMap<>();
        this.entityLoader = new EntityLoader(jdbcTemplate);
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }

    @Override
    public <T> Optional<T> getEntity(Class<T> clazz, Long id) {
        Object cachedEntity = entityCache.get(id);
        if (cachedEntity != null) {
            return Optional.of((T) cachedEntity);
        }
        Optional<T> searchedEntity = entityLoader.find(clazz, id);
        if (searchedEntity.isEmpty()) {
            return Optional.empty();
        }
        entityCache.put(id, searchedEntity.get());
        return searchedEntity;
    }

    @Override
    public void addEntity(Object entity) {
        entityPersister.insert(entity);
    }

    @Override
    public void removeEntity(Object entity) {
        Long deletedEntityId = entityPersister.delete(entity);
        entityCache.remove(deletedEntityId);
    }
}
