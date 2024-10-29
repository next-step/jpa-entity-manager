package persistence.entity;

import jakarta.persistence.EntityExistsException;
import persistence.model.EntityPrimaryKey;

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
        if (isEntityExists(entity)) {
            throw new EntityExistsException("ENTITY ALREADY EXISTS!");
        }
        entityPersister.insert(entity);
        persistenceContext.addEntity(entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
    }

    @Override
    public void merge(Object entity) {
        if (isEntityExists(entity)) {
            entityPersister.update(entity);
        } else {
            persist(entity);
        }
    }

    private Boolean isEntityExists(Object entity) {
        Class<?> entityClass = entity.getClass();
        Object entityId = EntityPrimaryKey.build(entity).keyValue();
        Object existingEntity = persistenceContext.getEntity(entityClass, entityId);

        return existingEntity != null;
    }
}
