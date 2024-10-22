package persistence.entity;

import jdbc.JdbcTemplate;

import java.util.List;

public class EntityManagerImpl implements EntityManager {
    private final JdbcTemplate jdbcTemplate;
    private final PersistenceContext persistenceContext;
    private final EntityLoader entityLoader;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate, PersistenceContext persistenceContext) {
        this.jdbcTemplate = jdbcTemplate;
        this.persistenceContext = persistenceContext;
        this.entityLoader = new EntityLoader(jdbcTemplate, persistenceContext);
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        final EntityKey entityKey = new EntityKey((Long) id, clazz);
        return entityLoader.loadEntity(clazz, entityKey);
    }

    @Override
    public void persist(Object entity) {
        final EntityPersister entityPersister = new EntityPersister(entity.getClass(), persistenceContext, jdbcTemplate);
        if (persistenceContext.isManagedEntity(entity, entityPersister.getEntityId(entity))) {
            return;
        }

        final EntityKey entityKey = entityPersister.insert(entity);

        persistenceContext.addEntity(entityKey, entity);
        persistenceContext.addDatabaseSnapshot(entityKey, entity);
    }

    @Override
    public void remove(Object entity) {
        final EntityPersister entityPersister = new EntityPersister(entity.getClass(), persistenceContext, jdbcTemplate);
        final EntityKey entityKey = new EntityKey(
                (Long) entityPersister.getEntityId(entity),
                entity.getClass()
        );

        entityPersister.delete(entity);
        persistenceContext.removeEntity(entityKey);
    }

    @Override
    public void update(Object entity) {
        final EntityPersister entityPersister = new EntityPersister(entity.getClass(), persistenceContext, jdbcTemplate);
        final EntityKey entityKey = new EntityKey(
                entityPersister.getEntityId(entity),
                entity.getClass()
        );

        final EntitySnapshot entitySnapshot = persistenceContext.getDatabaseSnapshot(entityKey, entity);
        if (entitySnapshot == null) {
            throw new IllegalStateException("Entity is not managed");
        }
        final Object managedEntity = persistenceContext.getEntity(entityKey);

        List<String> dirtyColumns = entitySnapshot.getDirtyColumns(managedEntity);
        entityPersister.update(entity, dirtyColumns);
        persistenceContext.addEntity(entityKey, entity);
        persistenceContext.addDatabaseSnapshot(entityKey, entity);
    }
}
