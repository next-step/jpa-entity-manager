package persistence.sql.entity;

import java.util.Objects;

public class EntityManagerImpl implements EntityManager {

    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(final EntityPersister entityPersister, EntityLoader entityLoader) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = new PersistenceContextImpl();
    }

    @Override
    public <T> T find(final Class<T> clazz, final Long id) {
        if (Objects.isNull(persistenceContext.getEntity(id))) {
            final T instance = entityLoader.findById(clazz, id);
            persistenceContext.addEntity(id, instance);
            return instance;
        }

        return (T) persistenceContext.getEntity(id);
    }

    @Override
    public Object persist(final Object entity) {
        final Long id = entityPersister.insert(entity);

        persistenceContext.addEntity(id, entity);
        return entityLoader.findById(entity.getClass(), id);
    }

    @Override
    public void remove(final Object entity) {
        persistenceContext.removeEntity(entity);
        entityPersister.delete(entity);
    }
}
