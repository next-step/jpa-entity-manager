package persistence.entity;

import jdbc.JdbcTemplate;

import java.util.List;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate,
                             PersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
        this.entityPersister = new EntityPersister(jdbcTemplate);
        this.entityLoader = new EntityLoader(jdbcTemplate, persistenceContext);
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        final EntityKey entityKey = new EntityKey((Long) id, clazz);
        return entityLoader.loadEntity(clazz, entityKey);
    }

    @Override
    public void persist(Object entity) {
        if (persistenceContext.isManagedEntity(entity, entityPersister.getEntityId(entity))) {
            return;
        }

        final EntityKey entityKey = entityPersister.insert(entity);

        persistenceContext.addEntity(entityKey, entity);
        persistenceContext.addDatabaseSnapshot(entityKey, entity);
    }

    @Override
    public void remove(Object entity) {
        final EntityKey entityKey = new EntityKey(
                entityPersister.getEntityId(entity),
                entity.getClass()
        );

        entityPersister.delete(entity);
        persistenceContext.removeEntity(entityKey);
    }

    @Override
    public void update(Object entity) {
        if (!persistenceContext.isManagedEntity(entity, entityPersister.getEntityId(entity))) {
            persist(entity);
            return;
        }

        final EntityKey entityKey = new EntityKey(
                entityPersister.getEntityId(entity),
                entity.getClass()
        );

        final EntitySnapshot entitySnapshot = persistenceContext.getDatabaseSnapshot(entityKey, entity);
        persistenceContext.addEntity(entityKey, entity);

        List<String> dirtyColumns = entitySnapshot.getDirtyColumns(persistenceContext.getEntity(entityKey));

        if (dirtyColumns.isEmpty()) {
            return;
        }
        entityPersister.update(entity, dirtyColumns);
        persistenceContext.addEntity(entityKey, entity);
        persistenceContext.addDatabaseSnapshot(entityKey, entity);
    }
}
