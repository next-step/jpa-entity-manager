package persistence;

import java.util.List;

public class EntityManagerImpl implements EntityManager {

    private final EntityPersister entityPersister;

    public EntityManagerImpl(EntityPersister entityPersister) {
        this.entityPersister = entityPersister;
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return this.entityPersister.find(clazz, id);
    }

    @Override
    public <T> List<T> findAll(Class<T> clazz) {
        return this.entityPersister.findAll(clazz);
    }

    @Override
    public void persist(Object entityInstance) {
        this.entityPersister.persist(entityInstance);
    }

    @Override
    public void update(Object entityInstance) {
        this.entityPersister.update(entityInstance);
    }

    @Override
    public void remove(Object entityInstance) {
        this.entityPersister.remove(entityInstance);
    }
}
