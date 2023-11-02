package persistence.entity;


import java.util.List;


public class SimpleEntityManager implements EntityManager {
    private final EntityLoaderContext entityLoaderContext;
    private final EntityPersisteContext entityPersisterContenxt;
    private final EntityEntryContext entityEntryContext;
    private final SimplePersistenceContext persistenceContext;

    private SimpleEntityManager(EntityPersisteContext entityPersisterContenxt,
                                EntityLoaderContext entityLoaderContext) {
        this.persistenceContext = new SimplePersistenceContext();
        this.entityEntryContext = new EntityEntryContext();
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
        EntityKey entityKey = EntityKey.of(entity);
        EntityEntry entityEntry = entityEntryContext.getEntityEntry(entityKey);

        final Object saveEntity = entityEntry.saving(entityPersister, entity);

        entityEntry.managed(persistenceContext, saveEntity);
        entityEntryContext.addEntityEntry(entityKey, entityEntry);

        return (T) saveEntity;
    }

    @Override
    public void remove(Object entity) {
        EntityKey entityKey = EntityKey.of(entity);
        EntityEntry entityEntry = entityEntryContext.getEntityEntry(entityKey);

        entityEntry.deleted(persistenceContext, entity);
        entityEntryContext.addEntityEntry(entityKey, entityEntry);
        System.out.println(entityEntry);
    }

    @Override
    public <T, ID> T find(Class<T> clazz, ID id) {
        EntityLoader entityLoader = entityLoaderContext.getEntityLoader(clazz);
        EntityKey entityKey = EntityKey.of(clazz, id);
        EntityEntry entityEntry = entityEntryContext.getEntityEntry(entityKey);

        if (entityEntry.isManaged()) {
            return (T) persistenceContext.getEntity(entityKey);
        }

        final T loading = entityEntry.loading(entityLoader, clazz, id);
        if (loading != null) {
            entityEntry.managed(persistenceContext, loading);
        }
        return loading;
    }

    @Override
    public <T> List<T> findAll(Class<T> tClass) {
        EntityLoader entityLoader = entityLoaderContext.getEntityLoader(tClass);
        final List<T> findList = entityLoader.findAll(tClass);

        findList.forEach((it) -> {
            EntityEntry entityEntry = entityEntryContext.getEntityEntry(EntityKey.of(it));
            entityEntry.managed(persistenceContext, it);
        });
        return findList;
    }


    @Override
    public void flush() {
        deleteEntity();
        dutyCheck();
    }

    private void deleteEntity() {
        entityEntryContext
                .getDeletedEntityKey()
                .forEach((it) -> {
                    EntityPersister entityPersister = entityPersisterContenxt.getEntityPersister(it.getClazz());
                    entityPersister.deleteByKey(it);
                });
        entityEntryContext.clear();
    }

    private void dutyCheck() {
        persistenceContext.getChangedEntity().forEach((it) -> {
            EntityPersister entityPersister = entityPersisterContenxt.getEntityPersister(it.getClass());
            entityPersister.update(it);
        });
        persistenceContext.clear();
    }
}
