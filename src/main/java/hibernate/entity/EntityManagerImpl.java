package hibernate.entity;

public class EntityManagerImpl implements EntityManager {

    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public EntityManagerImpl(final EntityPersister entityPersister, final EntityLoader entityLoader) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
    }

    @Override
    public <T> T find(final Class<T> clazz, final Object id) {
        return entityLoader.find(EntityClass.getInstance(clazz), id);
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
