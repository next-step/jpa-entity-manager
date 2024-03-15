package persistence.entity.impl;

import java.util.List;
import jdbc.JdbcTemplate;
import persistence.entity.EntityLoader;
import persistence.entity.EntityManager;
import persistence.entity.EntityPersister;
import persistence.entity.PersistenceContext;
import persistence.sql.EntityMetadata;

public class EntityManagerImpl implements EntityManager {
    private final EntityPersister entityPersister;

    private final EntityLoader entityLoader;

    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(JdbcTemplate jdbcTemplate) {
        this(new EntityPersisterImpl(jdbcTemplate), new EntityLoaderImpl(jdbcTemplate),
            new PersistenceContextImpl());
    }

    public EntityManagerImpl(EntityPersister entityPersister, EntityLoader entityLoader,
        PersistenceContext persistenceContext) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T find(Class<T> entityClass, Object id) {
        T cachedEntity = persistenceContext.getEntity(entityClass, id);

        if (cachedEntity != null) {
            return cachedEntity;
        }

        T entity = entityLoader.select(entityClass, id);

        persistenceContext.addEntity(id, entity);
        persistenceContext.getDatabaseSnapshot(id, entity);

        return entity;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass) {
        List<T> entities = entityLoader.selectAll(entityClass);

        entities.forEach(entity -> {
            EntityMetadata entityMetadata = new EntityMetadata(entity.getClass());

            Object id = entityMetadata.getIdFrom(entity);

            persistenceContext.addEntity(id, entity);
            persistenceContext.getDatabaseSnapshot(id, entity);
        });

        return entities;
    }

    @Override
    public <T> T persist(T entity) {
        if (!isNew(entity)) {
            save(entity);
        } else {
            merge(entity);
        }

        return entity;
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
    }

    private boolean isNew(Object entity) {
        Class<?> entityClass = entity.getClass();

        EntityMetadata entityMetadata = new EntityMetadata(entityClass);

        Object id = entityMetadata.getIdFrom(entity);

        return id == null || notExists(entityClass, id);
    }

    private boolean notExists(Class<?> entityClass, Object id) {
        return persistenceContext.getEntity(entityClass, id) == null
            && !entityLoader.isExists(entityClass, id);
    }

    private <T> void save(T entity) {
        entityPersister.insert(entity);
        // TODO: add to persistence context
    }

    private <T> T merge(T entity) {
        EntityMetadata entityMetadata = new EntityMetadata(entity.getClass());

        Object id = entityMetadata.getIdFrom(entity);

        T databaseSnapshot = persistenceContext.getDatabaseSnapshot(id, entity);

        if (isDirty(entity, databaseSnapshot)) {

            entityPersister.update(entity);
            persistenceContext.addEntity(id, entity);
            persistenceContext.getDatabaseSnapshot(id, entity);
        }

        return entity;
    }

    private <T> boolean isDirty(T entity, T databaseSnapshot) {
        return !databaseSnapshot.equals(entity);
    }
}
