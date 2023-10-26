package hibernate.entity;

public class EntityManagerImpl implements EntityManager {

    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(
            final EntityPersister entityPersister,
            final EntityLoader entityLoader,
            final PersistenceContext persistenceContext
    ) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
    }

    // TODO : PersistenceContext에 addEntity가 구현되면 추가구현한다.
    @Override
    public <T> T find(final Class<T> clazz, final Object id) {
        Object persistenceContextEntity = persistenceContext.getEntity(new EntityKey(id, clazz));
        if (persistenceContextEntity != null) {
            return (T) persistenceContextEntity;
        }

        EntityClass<T> entityClass = EntityClass.getInstance(clazz);
        return entityLoader.find(entityClass, id);
    }

    @Override
    public void persist(final Object entity) {
        entityPersister.insert(entity);
    }

    @Override
    public void remove(final Object entity) {
        entityPersister.delete(entity);
    }
}
