package hibernate.entity;

import jdbc.JdbcTemplate;

public class EntityManagerImpl implements EntityManager {

    private final JdbcTemplate jdbcTemplate;
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public EntityManagerImpl(final JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityLoader = new EntityLoader(jdbcTemplate);
        this.entityPersister = new EntityPersister(jdbcTemplate);
    }

    @Override
    public <T> T find(final Class<T> clazz, final Object id) {
        return entityLoader.find(EntityClass.getInstance(clazz), id);
    }

    @Override
    public void persist(final Object entity) {
        entityPersister.insert(entity);
    }

    @Override
    public void remove(final Object entity) {
        entityPersister.delete(entity);
    }
}
