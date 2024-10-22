package persistence.sql.entity;

import java.sql.Connection;

public class EntityManagerImpl implements EntityManager {
    private final EntityPersister entityPersister;
    private final PersistenceContext persistenceContext;
    private final EntityLoader entityLoader;

    public EntityManagerImpl(EntityPersister entityPersister, PersistenceContext persistenceContext, Connection connection) {
        this.entityPersister = entityPersister;
        this.persistenceContext = persistenceContext;
        this.entityLoader = new EntityLoader(connection);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        if (persistenceContext.containsEntity(clazz, id)) {
            return persistenceContext.getEntity(clazz, id);
        }

        String selectQuery = entityPersister.select(id);
        return entityLoader.loadEntity(clazz, selectQuery);
    }

    @Override
    public Object persist(Object entity) {
        Long idValue = entityPersister.getIdValue(entity);
        if (idValue == null) {
            String insertQuery = entityPersister.insert(entity);
            entityLoader.load(insertQuery);
            idValue = entityPersister.getIdValue(entity);
        }
        String updateQuery = entityPersister.update(entity);
        entityLoader.load(updateQuery);

        persistenceContext.addEntity(entity, idValue);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        Long idValue = entityPersister.getIdValue(entity);
        if (idValue == null) {
            return;
        }

        String deleteQuery = entityPersister.delete(entity);
        entityLoader.load(deleteQuery);

        persistenceContext.removeEntity(entity.getClass(), idValue);
    }

    @Override
    public Object update(Object entity) {
        Long idValue = entityPersister.getIdValue(entity);
        String updateQuery = entityPersister.update(entity);
        entityLoader.load(updateQuery);

        persistenceContext.addEntity(entity.getClass(), idValue);
        return entity;
    }

}