package persistence;

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
    public void persist(Object entityInstance) {
        this.entityPersister.persist(entityInstance);
    }

    @Override
    public void merge(Object entityInstance) {
        this.entityPersister.merge(entityInstance);
    }

    @Override
    public void remove(Object entityInstance) {
        this.entityPersister.remove(entityInstance);
    }
}
