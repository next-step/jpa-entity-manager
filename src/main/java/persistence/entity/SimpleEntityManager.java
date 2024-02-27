package persistence.entity;

import jdbc.JdbcTemplate;

public class SimpleEntityManager implements EntityManager {

    private final EntityPersister entityPersister;

    private final EntityLoader entityLoader;

    private SimpleEntityManager(JdbcTemplate jdbcTemplate) {
        entityPersister = SimpleEntityPersister.from(jdbcTemplate);
        entityLoader = SimpleEntityLoader.from(jdbcTemplate);
    }

    public static SimpleEntityManager from(JdbcTemplate jdbcTemplate) {
        return new SimpleEntityManager(jdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return entityLoader.find(clazz, id);
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
