package jpa;

public class EntityManagerImpl implements EntityManager {
    private final EntityPersister entityPersister;
    private final PersistenceContext persistenceContext;
    private final EntityLoader entityLoader;

    public EntityManagerImpl(EntityPersister entityPersister, EntityLoader entityLoader) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = new PersistenceContextImpl();
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        T entityPersistenceContext = persistenceContext.get(clazz, id);
        if (entityPersistenceContext != null) {
            return entityPersistenceContext;
        }

        T entity = entityLoader.find(clazz, id);
        persistenceContext.add(entity);
        return entity;
    }

    @Override
    public void persist(Object entity) {
        entityPersister.insert(entity);
        persistenceContext.add(entity);
    }

    @Override
    public void update(Object entity) {
        entityPersister.update(entity);
        persistenceContext.add(entity);
    }


    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
        persistenceContext.remove(entity);
    }


}
