package persistence.entity;

public class SimpleEntityManager implements EntityManager{
    private final EntityPersister entityPersister;

    private final PersistenceContext persistenceContext;

    public SimpleEntityManager(EntityPersister entityPersister, PersistenceContext persistenceContext) {
        this.entityPersister = entityPersister;
        this.persistenceContext = persistenceContext;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return persistenceContext.getEntity(clazz, id);
    }

    @Override
    public void persist(Object entity) {
        entityPersister.insert(entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }
}
