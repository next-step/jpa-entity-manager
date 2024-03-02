package persistence.entity;

public class SimpleEntityManger implements EntityManager {

    private final EntityPersister persister;

    public SimpleEntityManger(EntityPersister persister) {
        this.persister = persister;
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        return persister.read(clazz, id);
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
