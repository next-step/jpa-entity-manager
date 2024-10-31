package persistence.entity;

import jakarta.persistence.EntityExistsException;
import persistence.model.EntityPrimaryKey;
import persistence.util.ReflectionUtil;

public class EntityManagerImpl implements EntityManager {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(
            EntityPersister entityPersister,
            EntityLoader entityLoader,
            PersistenceContext persistenceContext
    ) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        T entity = persistenceContext.getEntity(clazz, id);
        if (entity != null) {
            return entity;
        }
        try {
            T foundEntity = entityLoader.find(clazz, id);
            persistenceContext.addEntity(foundEntity);
            return foundEntity;
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public void persist(Object entity) {
        EntityPrimaryKey pk = EntityPrimaryKey.build(entity);

        boolean existsInContext = persistenceContext.isEntityExists(entity);
        boolean existsInDatabase = pk.isValid() && entityLoader.exists(entity.getClass(), pk.keyValue());

        if (existsInContext || existsInDatabase) {
            throw new EntityExistsException("ENTITY ALREADY EXISTS!");
        }
        Object generatedId = entityPersister.insert(entity);
        ReflectionUtil.setFieldValue(entity, pk.keyName(), generatedId);
        persistenceContext.addEntity(entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
    }

    @Override
    public <T> T merge(T entity) {
        if (persistenceContext.isEntityExists(entity)) {
            entityPersister.update(entity);
            return entity;
        }
        persist(entity);
        return entity;
    }
}
