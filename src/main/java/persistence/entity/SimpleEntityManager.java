package persistence.entity;

public class SimpleEntityManager implements EntityManager {

    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public SimpleEntityManager(EntityPersister entityPersister, EntityLoader entityLoader) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return entityLoader.findById(clazz, id);
    }

    public Object persist(Object object) {
        return entityPersister.insert(object);
    }

    @Override
    public boolean update(Object object) {
        return entityPersister.update(object);
    }

    @Override
    public void remove(Object object) {
        entityPersister.delete(object);
    }
}
