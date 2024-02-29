package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.persistencecontext.EntitySnapshot;
import persistence.persistencecontext.MyPersistenceContext;
import persistence.persistencecontext.PersistenceContext;

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
        return persistenceContext.getEntity(clazz, id)
                .orElseGet(() -> {
                    T foundEntity = entityLoader.find(clazz, id);
                    EntityMeta entityMeta = EntityMeta.from(foundEntity);
                    addToCache(entityMeta);
                    return foundEntity;
                });
    }

    @Override
    public <T> T persist(T entity) {
        EntityMeta entityMeta = EntityMeta.from(entity);
        entityMeta.updateStatus(EntityStatus.SAVING);
        Object generatedId = entityPersister.insert(entityMeta);
        entityMeta.injectId(generatedId);
        addToCache(entityMeta);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        EntityMeta entityMeta = EntityMeta.from(entity);
        entityMeta.updateStatus(EntityStatus.DELETED);
        persistenceContext.removeEntity(entityMeta);
        entityMeta.updateStatus(EntityStatus.GONE);
        entityPersister.delete(entityMeta);
    }

    @Override
    public <T> T merge(T entity) {
        EntityMeta entityMeta = EntityMeta.from(entity);
        EntitySnapshot snapshot = (EntitySnapshot) persistenceContext.getCachedDatabaseSnapshot(entityMeta);
        if (snapshot.isChanged(entity)) {
            entityPersister.update(entityMeta);
        }
        addToCache(entityMeta);
        return entity;
    }

    private void addToCache(EntityMeta entityMeta) {
        entityMeta.updateStatus(EntityStatus.MANAGED);
        persistenceContext.addEntity(entityMeta);
        persistenceContext.getDatabaseSnapshot(entityMeta);
    }

    public PersistenceContext getPersistenceContext() {
        return persistenceContext;
    }
}
