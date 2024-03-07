package persistence.entity;

public class SimpleEntityManager implements EntityManager {

    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public SimpleEntityManager(final EntityPersister entityPersister, final EntityLoader entityLoader) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
    }

    @Override
    public <T> T find(final Class<T> clazz, final Object key) {
        return entityLoader.load(clazz, key);
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
