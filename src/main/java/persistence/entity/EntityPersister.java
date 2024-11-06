package persistence.entity;

import java.util.HashMap;
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

    public void update(Object entity) throws IllegalAccessException {
        getEntityQueryHandler(entity.getClass())
            .update(entity);
    }

    public void insert(Object entity) throws IllegalAccessException {
        getEntityQueryHandler(entity.getClass())
            .insert(entity);
    }

    private EntityQueryHandler<?> getEntityQueryHandler(Class<?> entityClass) {
        if (!entityQueryHandlerMap.containsKey(entityClass)) {
            entityQueryHandlerMap.put(entityClass, new EntityQueryHandler<>(entityClass, jdbcTemplate));
        }
        return entityQueryHandlerMap.get(entityClass);
    }


}
