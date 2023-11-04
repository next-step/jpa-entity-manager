package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dbms.Dialect;

public class JdbcEntityManager implements EntityManager {
    private final EntityManagementCache entityManagementCache;

    public JdbcEntityManager(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.entityManagementCache = new EntityManagementCache(jdbcTemplate, dialect);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        EntityLoader<T> entityLoader = entityManagementCache.loader(clazz);

        return entityLoader.findById(clazz, id);
    }

    public <T> T findByEntity(T entity) {
        EntityLoader<T> entityLoader = (EntityLoader<T>) entityManagementCache.loader(entity.getClass());

        return entityLoader.findOne(entity);
    }

    @Override
    public void persist(Object entity) {
        EntityPersister entityPersister = entityManagementCache.persister(entity.getClass());
        Object persistedEntity = findByEntity(entity);

        if (persistedEntity != null) {
            entityPersister.update(entity);
            return;
        }

        entityPersister.insert(entity);
    }

    @Override
    public void remove(Object entity) {
        EntityPersister entityPersister = entityManagementCache.persister(entity.getClass());

        entityPersister.delete(entity);
    }
}
