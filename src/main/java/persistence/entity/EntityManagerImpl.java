package persistence.entity;

public class EntityManagerImpl implements EntityManager {

    private final EntityPersister entityPersister;

    private final EntityLoader entityLoader;

    private final PersistenceContext persistenceContext;

    private final EntityEntryContext entityEntryContext;

    public EntityManagerImpl(EntityPersister entityPersister,
                             EntityLoader entityLoader, PersistenceContext persistenceContext) {
        this.entityPersister = entityPersister;
        this.entityLoader = entityLoader;
        this.persistenceContext = persistenceContext;
        this.entityEntryContext = new EntityEntryContext();
    }


    @Override
    public <T> T persist(T entity) {
        EntityEntry entry = entityEntryContext.get(entity);
        persist(entity, entry);
        persistenceContext.addEntity(entity);
        return entity;
    }

    private <T> void persist(T entity, EntityEntry entry) {
        entry.prePersist();
        entityPersister.insert(entity);
        entry.postPersist();
    }

    @Override
    public <T> T merge(T entity) {
        saveOrUpdate(entity);
        persistenceContext.addEntity(entity);
        return entity;
    }

    private void saveOrUpdate(Object entity) {
        EntityEntry entry = entityEntryContext.get(entity);
        if (persistenceContext.isDirty(entity)) {
            entry.preUpdate();
            entityPersister.update(entity);
            entry.postUpdate();
            return;
        }
        persist(entity, entry);
    }

    @Override
    public void remove(Object entity) {
        EntityEntry entry = entityEntryContext.get(entity);
        entry.preRemove();
        entityPersister.delete(entity);
        entry.postRemove();
        persistenceContext.removeEntity(entity);
    }


    @Override
    public <T> T find(Class<T> clazz, Object id) {
        T cachedEntity = persistenceContext.getEntity(clazz, id);
        if (cachedEntity != null) {
            return cachedEntity;
        }
        EntityEntry entry = new SimpleEntityEntry();
        entry.preLoad();
        T foundEntity = entityLoader.find(clazz, id);
        entry.postLoad();
        entityEntryContext.add(foundEntity, entry);
        persistenceContext.addEntity(foundEntity);
        return foundEntity;
    }

}
