package persistence.entity;

import database.sql.util.EntityMetadata;
import jdbc.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

public class EntityManagerImpl implements EntityManager {
    private final JdbcTemplate jdbcTemplate;
    private final Map<Class<?>, EntityPersister> entityPersisters;
    private final Map<Class<?>, EntityLoader> entityLoaders;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        entityPersisters = new HashMap<>();
        entityLoaders = new HashMap<>();
    }

    @Override
    public <T> T find(Class<T> entityClass, Long id) {
        EntityLoader entityLoader = getEntityLoader(entityClass);
        return (T) entityLoader.load(id);
    }

    @Override
    public void persist(Object entity) {
        EntityPersister entityPersister = getEntityPersister(entity.getClass());

        if (getId(entity) == null) {
            entityPersister.insert(entity);
            return;
        }
        entityPersister.update(entity);
    }

    @Override
    public void remove(Object entity) {
        getEntityPersister(entity.getClass()).delete(entity);
    }

    private EntityPersister getEntityPersister(Class<?> entityClass) {
        if (!entityPersisters.containsKey(entityClass)) {
            entityPersisters.put(entityClass, new EntityPersister(jdbcTemplate, entityClass));
        }
        return entityPersisters.get(entityClass);
    }

    private EntityLoader getEntityLoader(Class<?> entityClass) {
        if (!entityLoaders.containsKey(entityClass)) {
            entityLoaders.put(entityClass, new EntityLoader(jdbcTemplate, entityClass));
        }
        return entityLoaders.get(entityClass);
    }

    private static Long getId(Object entity) {
        EntityMetadata entityMetadata = new EntityMetadata(entity.getClass());
        return entityMetadata.getPrimaryKeyValue(entity);
    }
}
