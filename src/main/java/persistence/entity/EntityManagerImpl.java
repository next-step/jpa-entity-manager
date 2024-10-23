package persistence.entity;

import jdbc.JdbcTemplate;

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
        if (entityPersister.hasId(entity)) {
            throw new IllegalArgumentException("Entity already persisted");
        }

        final Object saved = entityPersister.insert(entity);
        final EntityKey entityKey = new EntityKey(
                entityPersister.getEntityId(saved),
                saved.getClass()
        );

        persistenceContext.addEntity(entityKey, saved);
        persistenceContext.addDatabaseSnapshot(entityKey, saved);
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
    public <T> T merge(T entity) {
        if (persistenceContext.isEntityAbsent(entity, entityPersister.getEntityId(entity))) {
            throw new IllegalStateException("Can not find entity in persistence context: "
                    + entity.getClass().getSimpleName());
        }

        final EntityKey entityKey = new EntityKey(
                entityPersister.getEntityId(entity),
                entity.getClass()
        );

        final EntitySnapshot entitySnapshot = persistenceContext.getDatabaseSnapshot(entityKey, entity);

        if (entitySnapshot.hasDirtyColumns(persistenceContext.getEntity(entityKey))) {
            entityPersister.update(entity);
        }

        persistenceContext.addEntity(entityKey, entity);
        persistenceContext.addDatabaseSnapshot(entityKey, entity);
        return entity;
    }
}
