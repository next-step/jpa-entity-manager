package persistence.entity;

import jdbc.JdbcTemplate;

import java.io.Serializable;

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
        final EntityKey entityKey = new EntityKey((Serializable) id, clazz);
        return entityLoader.loadEntity(clazz, entityKey);
    }

    @Override
    public void persist(Object entity) {
        final EntityPersister entityPersister = new EntityPersister(entity.getClass(), jdbcTemplate);
        final EntityKey entityKey = new EntityKey(
                (Serializable) entityPersister.getEntityId(entity),
                entity.getClass()
        );

        if (persistenceContext.getEntity(entityKey) != null) {
            return;
        }

        entityPersister.insert(entity);
        persistenceContext.addEntity(entityKey, entity);
    }

    @Override
    public void remove(Object entity) {
        final EntityPersister entityPersister = new EntityPersister(entity.getClass(), jdbcTemplate);
        final EntityKey entityKey = new EntityKey(
                (Serializable) entityPersister.getEntityId(entity),
                entity.getClass()
        );

        entityPersister.delete(entity);
        persistenceContext.removeEntity(entityKey);
    }

    @Override
    public void update(Object entity) {
        final EntityPersister entityPersister = new EntityPersister(entity.getClass(), jdbcTemplate);
        final EntityKey entityKey = new EntityKey(
                (Serializable) entityPersister.getEntityId(entity),
                entity.getClass()
        );

        entityPersister.update(entity);
        persistenceContext.addEntity(entityKey, entity);
    }
}
