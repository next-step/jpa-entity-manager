package persistence.entity;

import java.util.HashMap;
import java.util.Map;
import jdbc.JdbcTemplate;

public class EntityPersisterStore {

    Map<Class<?>, EntityPersister> entityPersisters;
    JdbcTemplate jdbcTemplate;

    public EntityPersisterStore(JdbcTemplate jdbcTemplate) {
        this.entityPersisters = new HashMap<>();
        this.jdbcTemplate = jdbcTemplate;
    }

    public EntityPersister getEntityPersister(Class<?> entityClass) {
        if (!entityPersisters.containsKey(entityClass)) {
            entityPersisters.put(entityClass, new EntityPersister(entityClass, jdbcTemplate));
        }
        return entityPersisters.get(entityClass);
    }

}
