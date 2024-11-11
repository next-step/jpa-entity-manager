package persistence.entity.impl;

import java.util.List;
import persistence.entity.EntityKey;
import persistence.entity.LongTypeId;
import persistence.entity.PendingEntities;
import persistence.entity.PersistedEntities;
import persistence.entity.PersistenceContext;

public class PersistenceContextImpl implements PersistenceContext {

    private final PersistedEntities persistedEntities;
    private final PendingEntities pendingEntities;

    public PersistenceContextImpl() {
        persistedEntities = new PersistedEntities();
        pendingEntities = new PendingEntities();
    }

    @Override
    public <T> T getEntity(Class<T> entityClass, Object primaryKey) {
        EntityKey entityKey = new EntityKey((Long) primaryKey, entityClass.getName());
        return entityClass.cast(persistedEntities.findEntity(entityKey));
    }


    @Override
    public void addEntity(Object entity)  {
        if (new LongTypeId(entity).isEntityIdNull()) {
            pendingEntities.persistEntity(entity);
            return;
        }
        persistedEntities.persistEntity(getEntityKey(entity), entity);
    }

    @Override
    public void removeEntity(Object entity) {
        pendingEntities.evict(entity);
        persistedEntities.changeToDeleteState(getEntityKey(entity));
    }

    @Override
    public void detachEntity(Object entity) {
        persistedEntities.evict(entity);
    }

    @Override
    public List<Object> getSavingEntities() {
        return pendingEntities.getEntities();
    }

    @Override
    public List<Object> getDeletedEntities() {
        return persistedEntities.getDeletedEntities();
    }

    @Override
    public List<Object> getManagedEntities() {
        return persistedEntities.getManagedEntities();
    }

    @Override
    public List<String> findDirtyColumns(Object entity) throws IllegalAccessException {
        return persistedEntities.findDirtyColumns(entity);
    }

    @Override
    public void updateDatabaseSnapshot(Object entity) {
        persistedEntities.updateDatabaseSnapshot(entity);
    }


    @Override
    public void mangeEntity(Object entity) {
        pendingEntities.evict(entity);
        long id = new LongTypeId(entity).getId();
        persistedEntities.persistEntity(new EntityKey(id, entity.getClass().getName()), entity);
    }

    private EntityKey getEntityKey(Object entity) {
        return new EntityKey(
            new LongTypeId(entity).getId(),
            entity.getClass().getName()
        );
    }


}
