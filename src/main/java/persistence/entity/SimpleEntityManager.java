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
        final T persistEntity = entityPersister.insert(entity);

        this.persistenceContext.addEntity(EntityKey.of(entity), persistEntity);

        return persistEntity;
    }

    @Override
    public void remove(Object entity) {
        EntityPersister entityPersister = entityPersisterContenxt.getEntityPersister(entity.getClass());
        EntityKey entityKey = EntityKey.of(entity);

        if (persistenceContext.getEntity(entityKey) != null) {
            persistenceContext.removeEntity(entity);
        }

        entityPersister.delete(entity);
    }

    @Override
    public <T, ID> T find(Class<T> clazz, ID id) {
        EntityLoader entityLoader = entityLoaderContext.getEntityLoader(clazz);
        EntityKey entityKey = EntityKey.of(clazz, id);

        if (persistenceContext.getEntity(entityKey) != null) {
            return (T) persistenceContext.getEntity(entityKey);
        }

        final T entity = entityLoader.find(clazz, id);
        if (entity != null) {
            persistenceContext.addEntity(entityKey, entity);
        }

        return entity;
    }

    @Override
    public <T> List<T> findAll(Class<T> tClass) {
        EntityLoader entityLoader = entityLoaderContext.getEntityLoader(tClass);

        final List<T> findList = entityLoader.findAll(tClass);

        findList.forEach((it) ->
                persistenceContext.addEntity(EntityKey.of(it), it)
        );
        return findList;
    }


    @Override
    public void flush() {
        final List<Object> changedEntity = persistenceContext.getChangedEntity();

        changedEntity.forEach((it) -> {
            EntityPersister entityPersister = entityPersisterContenxt.getEntityPersister(it.getClass());
            entityPersister.update(it);
        });

        persistenceContext.clear();
    }
}
