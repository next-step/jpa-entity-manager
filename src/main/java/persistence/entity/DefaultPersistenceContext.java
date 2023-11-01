package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.nonNull;

public class DefaultPersistenceContext implements PersistenceContext {

    private final Map<EntityKey, Object> entityInstanceMap = new HashMap<>();
    private final Map<EntityKey, Object> entitySnapShotMap = new HashMap<>();
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    private static DefaultPersistenceContext INSTANCE;

    private DefaultPersistenceContext(EntityPersister entityPersister, EntityLoader entityLoader) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
    }

    public static DefaultPersistenceContext of(JdbcTemplate jdbcTemplate) {
        if (nonNull(INSTANCE)) {
            return INSTANCE;
        }
        INSTANCE = new DefaultPersistenceContext(EntityPersister.of(jdbcTemplate), EntityLoader.of(jdbcTemplate));
        return INSTANCE;
    }

    @Override
    public <T> T getEntity(Class<T> clazz, Long entityId) {
        EntityKey entityKey = EntityKey.of(clazz, entityId);
        Object entity = entityInstanceMap.get(entityKey);
        if (Objects.isNull(entity)) {
            entity = entityLoader.selectOne(clazz, entityId);
            entityInstanceMap.put(entityKey, entity);
        }
        return clazz.cast(entityInstanceMap.get(entityKey));
    }

    @Override
    public void addEntity(Object entity) {
        entityPersister.insert(entity);
        EntityKey entityKey = EntityKey.from(entity);
        entityInstanceMap.put(entityKey, entity);
        entitySnapShotMap.put(entityKey, ObjectUtils.copy(entity));
    }

    @Override
    public void removeEntity(Object entity) {
        EntityKey entityKey = EntityKey.from(entity);
        entityInstanceMap.remove(entityKey);
        entitySnapShotMap.remove(entityKey);
        entityPersister.delete(entity);
    }

    @Override
    public Object getDatabaseSnapshot(Long id, Object entity) {
        EntityKey entityKey = EntityKey.of(entity.getClass(), id);
        return entitySnapShotMap.put(entityKey, ObjectUtils.copy(entity));
    }

}
