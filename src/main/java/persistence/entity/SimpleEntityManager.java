package persistence.entity;

public class SimpleEntityManager implements EntityManager{
    private final EntityPersister entityPersister;

    private final EntityLoader entityLoader;

    public SimpleEntityManager(EntityPersister entityPersister, EntityLoader entityLoader) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return entityLoader.find(clazz, id);
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
