package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.persister.EntityPersister;

public class JdbcEntityManager implements EntityManager {
    private final JdbcTemplate jdbcTemplate;

    public JdbcEntityManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        EntityPersister entityPersister = new EntityPersister(clazz);
        return (T) entityPersister.find(id, jdbcTemplate);
    }

    @Override
    public Object persist(Object entity) {
        EntityPersister entityPersister = new EntityPersister(entity.getClass());
        entityPersister.insert(entity, jdbcTemplate);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        EntityPersister entityPersister = new EntityPersister(entity.getClass());
        entityPersister.delete(entity, jdbcTemplate);
    }
}
