package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.context.PersistenceContext;
import persistence.context.SimplePersistenceContext;
import persistence.sql.dialect.Dialect;

import java.util.Objects;

public class SimpleEntityManager implements EntityManager {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext = new SimplePersistenceContext();

    public SimpleEntityManager(JdbcTemplate jdbcTemplate, Dialect dialect) {
        this.entityPersister = new EntityPersister(jdbcTemplate, dialect);
        this.entityLoader = new EntityLoader(jdbcTemplate, dialect);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        T entity = persistenceContext.getEntity(clazz, id);

        if (Objects.nonNull(entity)) {
            return entity;
        }

        Object object = entityLoader.find(EntityId.of(clazz, id));
        persistenceContext.addEntity(id, object);
        return clazz.cast(object);
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
