package persistence.entity;

import persistence.loader.EntityLoader;
import persistence.persister.EntityPersister;

public class SimpleEntityManager implements EntityManager {
    private final EntityLoader entityLoader;
    private final EntityPersister entityPersister;

    public SimpleEntityManager(EntityLoader entityLoader, EntityPersister entityPersister) {
        this.entityLoader = entityLoader;
        this.entityPersister = entityPersister;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return (T) entityLoader.find(id);
    }

    @Override
    public Object persist(Object entity) {
        entityPersister.insert(entity);
        return entity;
    }

    @Override
    public Object update(Object entity) {
        entityPersister.update(entity);
        return entity;
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
    }
}
