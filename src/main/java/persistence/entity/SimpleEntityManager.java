package persistence.entity;

import java.util.Objects;
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
            EntityEntry entityEntry = EntityEntry.loading();
            cacheEntity(entity);
            persistenceContext.setEntityEntry(entity, entityEntry);
            entityEntry.managed();
        }
        return entity;
    }

    @Override
    public <T> T persist(T entity) {
        EntityEntry entityEntry = EntityEntry.saving();
        entityPersister.insert(entity);
        cacheEntity(entity);
        persistenceContext.setEntityEntry(entity, entityEntry);
        entityEntry.managed();
        return entity;
    }

    @Override
    public void remove(Object entity) {
        EntityEntry entityEntry = persistenceContext.getEntityEntry(entity);
        entityEntry.deleted();
        persistenceContext.removeEntity(entity);
        entityPersister.delete(entity);
        entityEntry.gone();
    }

    @Override
    public <T> T merge(T entity) {
        EntitySnapshot before = persistenceContext.getCachedDatabaseSnapshot(entity);
        EntitySnapshot after = EntitySnapshot.from(entity);

        if (!Objects.equals(before, after)) {
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
