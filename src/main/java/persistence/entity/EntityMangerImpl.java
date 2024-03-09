package persistence.entity;

public class EntityMangerImpl implements EntityManger {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;
    private final EntityEntryContext entityEntryContext;

    public EntityMangerImpl(
            EntityPersister entityPersister,
            EntityLoader entityLoader,
            PersistenceContext persistenceContext,
            EntityEntryContext entityEntryContext
    ) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
        this.entityEntryContext = entityEntryContext;
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        EntityKey entityKey = new EntityKey(clazz, id);
        Object cachedEntity = persistenceContext.getEntity(entityKey);
        if(cachedEntity != null) {
            return (T) cachedEntity;
        }
        T foundEntity = entityLoader.find(clazz, id);
        persistenceContext.addEntity(entityKey, foundEntity);

        return foundEntity;
    }

    @Override
    public Object persist(Object entity) {
        EntityKey entityKey = EntityKey.fromEntity(entity);
        if(entityKey.hasId()) {
            throw new EntityAlreadyExistsException(entityKey);
        }

        entityPersister.insert(entity);
        entityKey = EntityKey.fromEntity(entity);
        persistenceContext.addEntity(entityKey, entity);
        EntityEntry entityEntry = new EntityEntry(entityPersister, entityLoader, Status.MANAGED);
        entityEntryContext.addEntry(entityKey, entityEntry);

        return entity;
    }

    @Override
    public Object merge(Object entity) {
        entityPersister.update(entity);
        persistenceContext.addEntity(EntityKey.fromEntity(entity), entity);

        return entity;
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
    }
}
