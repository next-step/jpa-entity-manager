package persistence;

import jakarta.persistence.Entity;

public class DefaultEntityManager implements EntityManager {

    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;

    public DefaultEntityManager(EntityPersister entityPersister, EntityLoader entityLoader, PersistenceContext persistenceContext) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        if (id == null) {
            throw new IllegalArgumentException("[EntityManager] find: id is null");
        }

        T entity = (T) persistenceContext.getEntity(id);
        if (entity == null) {
            entity = entityLoader.load(clazz, id);
        }

        persistenceContext.getCachedDatabaseSnapshot(id, entity);
        return entity;
    }

    @Override
    public <T> T persist(Object entity) {
        validEntity(entity);

        Long id = (Long) entityPersister.insert(entity);
        persistenceContext.addEntity(id, entity);

        return (T) find(entity.getClass(), id);
    }

    @Override
    public void remove(Object entity) {
        validEntity(entity);

        entityPersister.delete(entity);
        persistenceContext.removeEntity(new IdMetadata(entity).getMetadata());
    }

    @Override
    public <T> T merge(Long id, T entity) {
        Object snapshot = persistenceContext.getCachedDatabaseSnapshot(id, entity);

        if (!entity.equals(snapshot)) {
            entityPersister.insert(entity);
            persistenceContext.addEntity(id, entity);
        }

        return entity;
    }

    private static void validEntity(Object entity) {
        Class<?> clazz = entity.getClass();
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("[EntityManager] persist: the instance is not an entity");
        }
    }
}
