package persistence.entity;

import jakarta.persistence.EntityExistsException;
import persistence.sql.model.Table;

import java.util.Objects;

public class SimpleEntityManager implements EntityManager {

    private final EntityPersister persister;
    private final EntityLoader loader;
    private final PersistenceContext persistenceContext;

    public SimpleEntityManager(EntityPersister persister, EntityLoader loader) {
        this.persister = persister;
        this.loader = loader;
        this.persistenceContext = new SimplePersistenceContext();
    }

    @Override
    public <T> T find(Class<T> clazz, EntityId id) {
        T cachedEntity = persistenceContext.getEntity(clazz, id);
        if (cachedEntity != null) {
            return cachedEntity;
        }

        T findEntity = loader.read(clazz, id);
        persistenceContext.addEntity(id, findEntity);
        return findEntity;
    }

    @Override
    public void persist(Object entity) {
        if (isExist(entity)) {
            throw new EntityExistsException();
        }
        EntityId id = persister.create(entity);
        persistenceContext.addEntity(id, entity);
    }

    @Override
    public void merge(Object entity) {
        if (!isExist(entity)) {
            EntityId id = persister.create(entity);
            persistenceContext.addEntity(id, entity);
            return;
        }

        if (isDirty(entity)) {
            EntityId id = persister.update(entity);
            persistenceContext.addEntity(id, entity);
        }
    }

    private boolean isExist(Object entity) {
        return persistenceContext.isCached(entity) || loader.isExist(entity);
    }

    private boolean isDirty(Object entity) {
        EntityBinder entityBinder = new EntityBinder(entity);
        EntityId id = entityBinder.getEntityId();

        Object snapshot = getDatabaseSnapshot(entity, id);
        EntityBinder snapshotBinder = new EntityBinder(snapshot);

        Table table = createTable(entity);
        return table.getColumns()
                .stream()
                .anyMatch(column -> {
                    Object entityValue = entityBinder.getValue(column);
                    Object snapshotValue = snapshotBinder.getValue(column);
                    return !Objects.equals(entityValue, snapshotValue);
                });
    }

    private Object getDatabaseSnapshot(Object entity, EntityId id) {
        Object snapshot = persistenceContext.getDatabaseSnapshot(id, entity);
        if (snapshot != null) {
            return snapshot;

        }

        Class<?> clazz = entity.getClass();
        return find(clazz, id);
    }

    private Table createTable(Object entity) {
        EntityMetaCache entityMetaCache = EntityMetaCache.INSTANCE;
        Class<?> clazz = entity.getClass();
        return entityMetaCache.getTable(clazz);
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.removeEntity(entity);
        persister.delete(entity);
    }
}
