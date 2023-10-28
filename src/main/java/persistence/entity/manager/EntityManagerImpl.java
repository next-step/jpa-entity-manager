package persistence.entity.manager;

import persistence.context.PersistenceContext;

public class EntityManagerImpl implements EntityManager {
    private final PersistenceContext persistenceContext;

    private EntityManagerImpl(PersistenceContext persistenceContext) {
        this.persistenceContext = persistenceContext;
    }

    public static EntityManagerImpl of(PersistenceContext persistenceContext) {
        return new EntityManagerImpl(persistenceContext);
    }

    @Override
    public <T> T findById(Class<T> clazz, String id) {
        return persistenceContext.getEntity(clazz, id);
    }

    @Override
    public <T> T persist(T instance) {
        return persistenceContext.addEntity(instance);
    }

    @Override
    public <T> void remove(T instance) {
        persistenceContext.removeEntity(instance);
    }

    @Override
    public <T, ID> T getDatabaseSnapshot(ID id, T entity) {
        return null;
    }

    @Override
    public <T, ID> T getCachedDatabaseSnapshot(Class<T> clazz, ID id) {
        return persistenceContext.getCachedDatabaseSnapshot(clazz, String.valueOf(id));
    }
}
