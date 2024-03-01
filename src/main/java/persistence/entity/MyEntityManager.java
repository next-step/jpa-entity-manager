package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.persistencecontext.EntitySnapshot;
import persistence.persistencecontext.MyPersistenceContext;
import persistence.persistencecontext.PersistenceContext;

import java.util.List;

public class MyEntityManager implements EntityManager {

    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public MyEntityManager(JdbcTemplate jdbcTemplate) {
        this.entityPersister = new MyEntityPersister(jdbcTemplate);
        this.entityLoader = new MyEntityLoader(jdbcTemplate);
        this.persistenceContext = new MyPersistenceContext();
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return (T) persistenceContext.getEntity(clazz, id)
                .orElseGet(() -> {
                    T foundEntity = entityLoader.find(clazz, id);
                    addToCache(foundEntity);
                    return foundEntity;
                });
    }

    @Override
    public <T> T persist(T entity) {
        persistenceContext.addEntityEntry(entity, EntityStatus.SAVING);
        Object generatedId = entityPersister.insert(entity);
        EntityMeta entityMeta = EntityMeta.from(entity);
        entityMeta.injectId(entity, generatedId);
        addToCache(entity);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.removeEntity(entity);
        entityPersister.delete(entity);
    }

    @Override
    public <T> T merge(T entity) {
        EntitySnapshot snapshot = (EntitySnapshot) persistenceContext.getCachedDatabaseSnapshot(entity);
        if (snapshot.isChanged(entity)) {
            persistenceContext.addEntity(entity);
        }
        return entity;
    }

    @Override
    public void flush() {
        List<Object> entities = persistenceContext.getDirtyEntities();
        for (Object entity : entities) {
            entityPersister.update(entity);
            persistenceContext.addEntityEntry(entity, EntityStatus.GONE);
        }
    }

    private void addToCache(Object entity) {
        persistenceContext.addEntity(entity);
        persistenceContext.getDatabaseSnapshot(entity);
    }
}
