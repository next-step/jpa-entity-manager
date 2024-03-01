package persistence.sql.entity;

public class EntityManagerImpl implements EntityManager {

    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;

    public EntityManagerImpl(final EntityPersister entityPersister, EntityLoader entityLoader) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
    }

    @Override
    public <T> T find(final Class<T> clazz, final Long id) {
        return entityLoader.findById(clazz, id);
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
