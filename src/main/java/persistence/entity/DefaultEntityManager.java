package persistence.entity;

import jdbc.JdbcTemplate;

public class DefaultEntityManager implements EntityManager {

    private final JdbcTemplate jdbcTemplate;
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    private DefaultEntityManager(JdbcTemplate jdbcTemplate, EntityPersister entityPersister, EntityLoader entityLoader) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
    }

    public static DefaultEntityManager of(JdbcTemplate jdbcTemplate) {
        return new DefaultEntityManager(jdbcTemplate, EntityPersister.of(jdbcTemplate), EntityLoader.of(jdbcTemplate));
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
