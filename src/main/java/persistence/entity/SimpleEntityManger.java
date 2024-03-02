package persistence.entity;

public class SimpleEntityManger implements EntityManager {

    private final EntityPersister persister;
    private final EntityLoader loader;

    public SimpleEntityManger(EntityPersister persister, EntityLoader loader) {
        this.persister = persister;
        this.loader = loader;
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        return loader.read(clazz, id);
    }

    @Override
    public void persist(Object entity) {
        if (persister.isExist(entity)) {
            persister.update(entity);
        }
        persister.create(entity);
    }

    @Override
    public void remove(Object entity) {
        persister.delete(entity);
    }
}
