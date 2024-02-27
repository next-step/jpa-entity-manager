package persistence.entity;

import jdbc.JdbcTemplate;

public class MyEntityManager implements EntityManager {

    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public MyEntityManager(JdbcTemplate jdbcTemplate) {
        this.entityPersister = new MyEntityPersister(jdbcTemplate);
        this.entityLoader = new MyEntityLoader(jdbcTemplate);
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
