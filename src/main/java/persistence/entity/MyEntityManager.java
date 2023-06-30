package persistence.entity;

import java.util.Optional;

public class MyEntityManager implements EntityManager {
    private final PersistenceContext persistenceContext;
    private final EntityPersister entityPersister;

    public MyEntityManager(EntityPersister entityPersister) {
        this.persistenceContext = new PersistenceContext();
        this.entityPersister = entityPersister;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        final T entity = (T) persistenceContext.getEntity(clazz, id);
        if (entity != null) {
            return entity;
        }
        final T loadedEntity = (T) entityPersister.load(clazz, id);
        persistenceContext.addEntity(loadedEntity);
        return loadedEntity;
    }

    @Override
    public void persist(Object entity) {
        if (persistenceContext.contains(entity)) {
            return;
        }
        persistenceContext.addEntity(entity);
        entityPersister.insert(entity);
    }

    @Override
    public void remove(Object entity) {
        if (!persistenceContext.contains(entity)) {
            return;
        }
        persistenceContext.removeEntity(entity);
        entityPersister.delete(entity);
    }


}
