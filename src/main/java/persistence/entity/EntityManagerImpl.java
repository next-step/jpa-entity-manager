package persistence.entity;

public class EntityManagerImpl implements EntityManager {

    private final EntityPersister entityPersister;

    private final EntityLoader entityLoader;


    public EntityManagerImpl(EntityPersister entityPersister,
                             EntityLoader entityLoader) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
    }


    @Override
    public void persist(Object entity) {
        entityPersister.insert(entity);
    }

    @Override
    public boolean update(Object entity, Object id) {
        return entityPersister.update(entity, id);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        return entityLoader.find(clazz, id);
    }

}
