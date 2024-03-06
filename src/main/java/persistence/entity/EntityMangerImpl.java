package persistence.entity;

public class EntityMangerImpl implements EntityManger {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;

    public EntityMangerImpl(
            EntityPersister entityPersister,
            EntityLoader entityLoader,
            PersistenceContext persistenceContext
    ) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
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
        boolean isEntityUpdated = entityPersister.update(entity);
        if (!isEntityUpdated) {
            entityPersister.insert(entity);
        }

        persistenceContext.addEntity(EntityKey.fromEntity(entity), entity);

        return entity;
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
    }
}
