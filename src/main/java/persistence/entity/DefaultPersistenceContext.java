package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.ColumnValues;
import persistence.sql.util.StringConstant;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

public class DefaultPersistenceContext implements PersistenceContext {

    private final Map<String, Object> entityInstanceMap = new HashMap<>();

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
        if (!entityInstanceMap.containsKey(entityKey)) {
            T entityInstance = entityLoader.selectOne(clazz, entityId);
            entityInstanceMap.put(entityKey, entityInstance);
        }
        return clazz.cast(entityInstanceMap.get(entityKey));
    }

    @Override
    public void addEntity(Object entity) {
        entityPersister.insert(entity);
        String entityKey = buildEntityKey(entity);
        entityInstanceMap.put(entityKey, entity);
    }

    @Override
    public void removeEntity(Object entity) {
        String entityKey = buildEntityKey(entity);
        entityInstanceMap.remove(entityKey);
        entityPersister.delete(entity);
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
