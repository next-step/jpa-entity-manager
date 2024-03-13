package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dialect.Dialect;

public class SimpleEntityManager implements EntityManager {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public SimpleEntityManager(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.entityPersister = new EntityPersister(jdbcTemplate, dialect);
        this.entityLoader = new EntityLoader(jdbcTemplate, dialect);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return entityLoader.find(new EntityId(clazz, id));
    }

    @Override
    public Object persist(Object entity) {
        try {
            Object findEntity = entityLoader.findByEntity(entity);
            if (!entity.equals(findEntity)) {
                entityPersister.update(entity);
            }
        } catch (RuntimeException e) {
            entityPersister.insert(entity);
        }

        return entity;
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }
}
