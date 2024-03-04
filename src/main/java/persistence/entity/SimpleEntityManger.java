package persistence.entity;

import jakarta.persistence.EntityExistsException;

public class SimpleEntityManger implements EntityManager {

    private final EntityPersister persister;
    private final EntityLoader loader;
    private final PersistenceContext persistenceContext;

    public SimpleEntityManger(EntityPersister persister, EntityLoader loader) {
        this.persister = persister;
        this.loader = loader;
        this.persistenceContext = new SimplePersistenceContext();
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        Object cachedEntity = persistenceContext.getEntity((Long) id);
        if (cachedEntity != null) {
            return (T) cachedEntity;
        }

        T findEntity = loader.read(clazz, id);
        persistenceContext.addEntity((Long) id, findEntity);
        return findEntity;
    }

    @Override
    public void persist(Object entity) {
        if (isExist(entity)) {
            throw new EntityExistsException();
        }
        persister.create(entity);
    }

    @Override
    public void merge(Object entity) {
        if (isExist(entity)) {
            persister.update(entity);
        }
        persister.create(entity);
    }

    private boolean isExist(Object entity) {
        return persistenceContext.isCached(entity) || loader.isExist(entity);
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.removeEntity(entity);
        persister.delete(entity);
    }
}
