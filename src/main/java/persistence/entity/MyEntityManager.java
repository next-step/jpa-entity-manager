package persistence.entity;

import persistence.sql.Id;

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
    public Object persist(Object entity) {
        if (persistenceContext.contains(entity)) {
            return merge(entity);
        }
        entityPersister.insert(entity);
        persistenceContext.addEntity(entity);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        if (!persistenceContext.contains(entity)) {
            return;
        }
        persistenceContext.removeEntity(entity);
        entityPersister.delete(entity);
    }

    @Override
    public Object merge(Object entity) {
        final Object cachedEntity = persistenceContext.getEntity(entity.getClass(), new Id(entity).getValue());
        if (cachedEntity.equals(entity)) {
            return cachedEntity;
        }
        persistenceContext.removeEntity(cachedEntity);
        entityPersister.update(entity);
        persistenceContext.addEntity(entity);
        return entity;
    }

    @Override
    public boolean contains(Object entity) {
        return persistenceContext.contains(entity);
    }


}
