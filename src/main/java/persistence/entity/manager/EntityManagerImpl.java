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
}
