package persistence.entity;


import java.util.List;


public class SimpleEntityManager implements EntityManager {
    private final EntityLoaderContext entityLoaderContext;
    private final EntityPersisteContext entityPersisterContenxt;
    private final SimplePersistenceContext persistenceContext;

    private SimpleEntityManager(EntityPersisteContext entityPersisterContenxt,
                                EntityLoaderContext entityLoaderContext) {
        this.persistenceContext = new SimplePersistenceContext();
        this.entityPersisterContenxt = entityPersisterContenxt;
        this.entityLoaderContext = entityLoaderContext;
    }

    public static SimpleEntityManager of(EntityPersisteContext entityPersisterContenxt,
                                         EntityLoaderContext entityLoaderContext) {
        return new SimpleEntityManager(entityPersisterContenxt, entityLoaderContext);
    }

    @Override
    public <T> T persist(T entity) {
        EntityPersister entityPersister = entityPersisterContenxt.getEntityPersister(entity.getClass());

        return persistenceContext.saving(entityPersister, entity);
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.deleted(entity);
    }

    @Override
    public <T, ID> T find(Class<T> clazz, ID id) {
        EntityLoader entityLoader = entityLoaderContext.getEntityLoader(clazz);

        return persistenceContext.loading(entityLoader, clazz, id);
    }

    @Override
    public <T> List<T> findAll(Class<T> tClass) {
        EntityLoader entityLoader = entityLoaderContext.getEntityLoader(tClass);

        return persistenceContext.findAll(entityLoader, tClass);
    }


    @Override
    public void flush() {
        persistenceContext.flush(entityPersisterContenxt);
    }

}
