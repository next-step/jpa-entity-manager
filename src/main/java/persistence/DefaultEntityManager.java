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

        persistenceContext.getDatabaseSnapshot(id, entity);

        return entity;
    }

    @Override
    public void persist(Object entity) {
        validEntity(entity);

        entityPersister.insert(entity);
    }

    @Override
    public void remove(Object entity) {
        validEntity(entity);

        entityPersister.delete(entity);
    }

    private static void validEntity(Object entity) {
        Class<?> clazz = entity.getClass();
        if (!clazz.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException("[EntityManager] persist: the instance is not an entity");
        }
    }
}
