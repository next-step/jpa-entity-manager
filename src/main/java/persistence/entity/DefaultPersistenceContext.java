package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.ColumnValues;
import persistence.sql.util.ObjectUtils;
import persistence.sql.util.StringConstant;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.nonNull;

public class DefaultPersistenceContext implements PersistenceContext {

    private final Map<String, Object> entityInstanceMap = new HashMap<>();
    private final Map<String, Object> entitySnapShotMap = new HashMap<>();
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
        String entityKey = buildEntityKey(clazz, entityId);
        Object entity = entityInstanceMap.get(entityKey);
        if (Objects.isNull(entity)) {
            entity = entityLoader.selectOne(clazz, entityId);
            entityInstanceMap.put(entityKey, entity);
        }
        return clazz.cast(entityInstanceMap.get(entityKey));
    }

    @Override
    public void addEntity(Object entity) {
        String entityKey = buildEntityKey(entity);
        entityPersister.insert(entity);
        entityInstanceMap.put(entityKey, entity);
        entitySnapShotMap.put(entityKey, ObjectUtils.copy(entity));
    }

    @Override
    public void removeEntity(Object entity) {
        String entityKey = buildEntityKey(entity);
        entityInstanceMap.remove(entityKey);
        entitySnapShotMap.remove(entityKey);
        entityPersister.delete(entity);
    }

    @Override
    public Object getDatabaseSnapshot(Long id, Object entity) {
        String entityKey = buildEntityKey(entity.getClass(), id);
        return entitySnapShotMap.put(entityKey, ObjectUtils.copy(entity));
    }

    private String buildEntityKey(Class<?> clazz, Object entityId) {
        return clazz.getName() + entityId.toString();
    }

    private String buildEntityKey(Object entity) {
        ColumnValues idValues = ColumnValues.ofId(entity);
        String entityId = String.join(StringConstant.EMPTY_STRING, idValues.values());
        return buildEntityKey(entity.getClass(), entityId);
    }

}
