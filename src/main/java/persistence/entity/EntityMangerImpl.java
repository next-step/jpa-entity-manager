package persistence.entity;

public class EntityMangerImpl implements EntityManger {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;
    private final EntityEntryContext entityEntryContext;
    private final EntityEntryFactory entityEntryFactory;


    public EntityMangerImpl(
            EntityPersister entityPersister,
            EntityLoader entityLoader,
            PersistenceContext persistenceContext,
            EntityEntryContext entityEntryContext,
            EntityEntryFactory entityEntryFactory
    ) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
        this.entityEntryContext = entityEntryContext;
        this.entityEntryFactory = entityEntryFactory;
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        EntityKey entityKey = new EntityKey(clazz, id);
        Object cachedEntity = persistenceContext.getEntity(entityKey);

        if(cachedEntity != null) {
            return (T) cachedEntity;
        }

        EntityEntry entityEntry = entityEntryFactory.createEntityEntry(Status.LOADING);
        entityEntryContext.addEntry(entityKey, entityEntry);

        T foundEntity = entityLoader.find(clazz, id);
        persistenceContext.addEntity(entityKey, foundEntity);

        entityEntry.setManaged();

        return foundEntity;
    }

    @Override
    public Object persist(Object entity) {
        EntityKey entityKey = EntityKey.fromEntity(entity);
        if(entityKey.hasId()) {
            throw new EntityAlreadyExistsException(entityKey);
        }

        // TODO: entityEntry 에 SAVING 상태로 등록. id 가 없는데 어떻게?

        entityPersister.insert(entity);
        entityKey = EntityKey.fromEntity(entity);
        persistenceContext.addEntity(entityKey, entity);
        EntityEntry entityEntry = entityEntryFactory.createEntityEntry(Status.MANAGED);
        entityEntryContext.addEntry(entityKey, entityEntry);

        return entity;
    }

    @Override
    public Object merge(Object entity) {
        EntityKey entityKey = EntityKey.fromEntity(entity);
        EntityEntry entityEntry = entityEntryContext.getEntry(entityKey);

        if(entityEntry == null || persistenceContext.getEntity(entityKey) == null) {
            throw new EntityNotExistsException(entityKey);
        }

        if(entityEntry.isReadOnly()){
            throw new EntityReadOnlyException(entityKey);
        }

        entityEntry.setSaving();
        entityPersister.update(entity);
        persistenceContext.addEntity(entityKey, entity);
        entityEntry.setManaged();

        return entity;
    }

    @Override
    public void remove(Object entity) {
        EntityKey entityKey = EntityKey.fromEntity(entity);
        EntityEntry entityEntry = entityEntryContext.getEntry(entityKey);

        entityEntry.setDeleted();
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
        entityEntry.setGone();
    }
}
