package persistence.entity.impl;

import java.util.List;
import jdbc.JdbcTemplate;
import persistence.entity.EntityLoader;
import persistence.entity.EntityManager;
import persistence.entity.EntityPersister;

public class EntityManagerImpl implements EntityManager {
    private final EntityPersister entityPersister;

    private final EntityLoader entityLoader;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this(new EntityPersisterImpl(jdbcTemplate), new EntityLoaderImpl(jdbcTemplate));
    }

    public EntityManagerImpl(EntityPersister entityPersister, EntityLoader entityLoader) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
    }

    @Override
    public <T> T find(Class<T> entityClass, Long id) {
        return entityLoader.select(entityClass, id);
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        return entityLoader.selectAll(entityClass);
    }

    @Override
    public Object persist(Object entity) {
        entityPersister.insert(entity);

        return entity;
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }
}
