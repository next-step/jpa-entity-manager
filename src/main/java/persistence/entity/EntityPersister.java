package persistence.entity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdbc.JdbcTemplate;

public class EntityPersister {

    Map<Class<?>, EntityQueryHandler<?>> entityQueryHandlerMap;
    JdbcTemplate jdbcTemplate;

    public EntityPersister(JdbcTemplate jdbcTemplate) {
        this.entityQueryHandlerMap = new HashMap<>();
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> Object findById(Class<T> entityClass, Object primaryKey) {
        return getEntityQueryHandler(entityClass)
            .findById(primaryKey);
    }

    public void update(Object entity, List<String> changedColumns) throws IllegalAccessException {
        getEntityQueryHandler(entity.getClass())
            .update(entity, changedColumns);
    }

    public Object insert(Object entity) throws IllegalAccessException {
        long id = getEntityQueryHandler(entity.getClass())
            .insert(entity);
        return setId(entity, id);
    }

    public void delete(Object entity) {
        getEntityQueryHandler(entity.getClass())
            .delete(entity);
    }

    private EntityQueryHandler<?> getEntityQueryHandler(Class<?> entityClass) {
        if (!entityQueryHandlerMap.containsKey(entityClass)) {
            entityQueryHandlerMap.put(entityClass, new EntityQueryHandler<>(entityClass, jdbcTemplate));
        }
        return entityQueryHandlerMap.get(entityClass);
    }

    private Object setId(Object entity, long id) throws IllegalAccessException {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
            return entity;
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("Entity does not have an 'id' field", e);
        }
    }

}
