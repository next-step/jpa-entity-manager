package persistence.entity;

import jdbc.JdbcTemplate;

public class DefaultEntityManager implements EntityManager {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    private DefaultEntityManager(EntityPersister entityPersister, EntityLoader entityLoader) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
    }

    public static DefaultEntityManager of(JdbcTemplate jdbcTemplate) {
        return new DefaultEntityManager(EntityPersister.of(jdbcTemplate), EntityLoader.of(jdbcTemplate));
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
