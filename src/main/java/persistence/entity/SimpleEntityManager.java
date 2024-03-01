package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.SimpleEntityLoader;
import persistence.entity.persistencecontext.EntitySnapshot;
import persistence.entity.persistencecontext.SimplePersistenceContext;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.SimpleEntityPersister;

public class SimpleEntityManager implements EntityManager {

    private final EntityPersister entityPersister;
    private final SimplePersistenceContext persistenceContext;

    private final EntityLoader entityLoader;

    private SimpleEntityManager(JdbcTemplate jdbcTemplate) {
        entityPersister = SimpleEntityPersister.from(jdbcTemplate);
        entityLoader = SimpleEntityLoader.from(jdbcTemplate);
        persistenceContext = new SimplePersistenceContext();
    }

    public static SimpleEntityManager from(JdbcTemplate jdbcTemplate) {
        return new SimpleEntityManager(jdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        T entity = (T) persistenceContext.getEntity(clazz, id);
        if (entity == null) {
            entity = entityLoader.find(clazz, id);
            cacheEntity(entity);
        }
        return entity;
    }

    @Override
    public <T> T persist(T entity) {
        entityPersister.insert(entity);
        cacheEntity(entity);

        return entity;
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.removeEntity(entity);
        entityPersister.delete(entity);
    }

    @Override
    public <T> T merge(T entity) {
        EntitySnapshot before = persistenceContext.getCachedDatabaseSnapshot(entity);
        EntitySnapshot after = EntitySnapshot.from(entity);

        if (before.equals(after)) {
            entityPersister.update(entity);
            cacheEntity(entity);
        }
        return entity;
    }

    private void cacheEntity(Object entity) {
        persistenceContext.addEntity(entity);
        persistenceContext.getDatabaseSnapshot(entity);
    }
}
