package persistence.entity;

import persistence.sql.domain.DatabaseTable;

public class EntityManagerImpl implements EntityManager {

    private final EntityPersister entityPersister;

    private final EntityLoader entityLoader;

    private final PersistenceContext persistenceContext;


    public EntityManagerImpl(EntityPersister entityPersister,
                             EntityLoader entityLoader, PersistenceContext persistenceContext) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
    }


    @Override
    public void persist(Object entity) {
        entityPersister.insert(entity);
        persistenceContext.addEntity(entity);
    }


    @Override
    public <T> T merge(T entity) {
        saveOrUpdate(entity);
        persistenceContext.addEntity(entity);
        return entity;
    }

    private void saveOrUpdate(Object entity) {
        if (persistenceContext.isDirty(entity)){
            entityPersister.update(entity);
            return;
        }
        entityPersister.insert(entity);
    }


    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
    }


    @Override
    public <T> T find(Class<T> clazz, Object id) {
        T cachedEntity = persistenceContext.getEntity(clazz, id);
        if (cachedEntity != null) {
            return cachedEntity;
        }
        T foundEntity = entityLoader.find(clazz, id);
        persistenceContext.addEntity(foundEntity);
        return foundEntity;
    }

}
