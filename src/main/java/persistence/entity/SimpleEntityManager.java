package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.loader.EntityLoader;
import persistence.persister.EntityPersister;

public class SimpleEntityManager implements EntityManager {
    private final JdbcTemplate jdbcTemplate;

    public SimpleEntityManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        EntityLoader entityLoader = new EntityLoader(clazz, jdbcTemplate);
        return (T) entityLoader.find(id);
    }

    @Override
    public Object persist(Object entity) {
        EntityPersister entityPersister = new EntityPersister(entity.getClass(), jdbcTemplate);
        entityPersister.insert(entity);
        return entity;
    }

    @Override
    public Object update(Object entity) {
        EntityPersister entityPersister = new EntityPersister(entity.getClass(), jdbcTemplate);
        entityPersister.update(entity);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        EntityPersister entityPersister = new EntityPersister(entity.getClass(), jdbcTemplate);
        entityPersister.delete(entity);
    }
}
