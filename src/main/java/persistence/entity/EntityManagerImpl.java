package persistence.entity;

import jdbc.JdbcTemplate;

public class EntityManagerImpl implements EntityManager {

    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this.entityPersister = new EntityPersisterImpl(jdbcTemplate);
        this.entityLoader = new EntityLoaderImpl(jdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> clazz, Long Id) {
        return entityLoader.find(clazz, Id);
    }

    @Override
    public void persist(Object entity) {
        entityPersister.insert(entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }
}
