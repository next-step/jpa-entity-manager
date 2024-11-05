package persistence.entity;

import jakarta.persistence.EntityExistsException;
import persistence.model.EntityPrimaryKey;
import persistence.util.ReflectionUtil;

public class EntityManagerImpl implements EntityManager {
    private final EntityPersister entityPersister;
    private final EntityLoader entityLoader;
    private final PersistenceContext persistenceContext;

    public EntityManagerImpl(
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
        T entity = persistenceContext.getEntity(clazz, id);
        if (entity != null) {
            return entity;
        }
        try {
            T foundEntity = entityLoader.find(clazz, id);
            persistenceContext.addEntity(foundEntity);
            return foundEntity;
        } catch (RuntimeException e) {
            return null;
        }
    }

    @Override
    public void persist(Object entity) {
        if (persistenceContext.isEntityExists(entity) || existsInDatabase(entity)) {
            throw new EntityExistsException("ENTITY ALREADY EXISTS!");
        }
        entityPersister.insert(entity);
        persistenceContext.addEntity(entity);
    }

    @Override
    public void remove(Object entity) {
        entityPersister.delete(entity);
        persistenceContext.removeEntity(entity);
    }

    @Override
    public <T> T merge(T entity) {
        if (persistenceContext.isEntityExists(entity)) {
            persistenceContext.updateEntity(entity);
            return entity;
        }
        if (existsInDatabase(entity)) {
            persistenceContext.addEntity(entity);
            EntitySnapshot snapshot = persistenceContext.getSnapshot(entity);
            snapshot.setDirty(true);
            return merge(entity);
        }
        persist(entity);
        return entity;
    }

    // XXX: 세부 구현은 X. 우선 update 동작하도록만 메소드 추가.
    @Override
    public void flush() {
        persistenceContext.getDirtySnapshots()
                .forEach(snapshot -> {
                    entityPersister.update(snapshot.getOriginalEntity());
                    snapshot.setDirty(false);
                });
    }

    private boolean existsInDatabase(Object entity) {
        EntityPrimaryKey pk = EntityPrimaryKey.build(entity);
        return pk.isValid() && entityLoader.exists(entity.getClass(), pk.keyValue());
    }
}
