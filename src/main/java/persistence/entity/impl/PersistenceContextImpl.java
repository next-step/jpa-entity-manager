package persistence.entity.impl;

import java.util.Collection;
import java.util.Set;
import persistence.entity.DatabaseSnapshots;
import persistence.entity.EntityKey;
import persistence.entity.LongTypeId;
import persistence.entity.PendingEntities;
import persistence.entity.PersistedEntities;
import persistence.entity.PersistenceContext;

public class PersistenceContextImpl implements PersistenceContext {

    private final PersistedEntities persistedEntities;
    private final PendingEntities pendingEntities;
    private final DatabaseSnapshots databaseSnapshots;

    public PersistenceContextImpl() {
        persistedEntities = new PersistedEntities();
        pendingEntities = new PendingEntities();
        databaseSnapshots = new DatabaseSnapshots();
    }

    @Override
    public <T> T getEntity(Class<T> entityClass, Object primaryKey) {
        EntityKey entityKey = new EntityKey((Long) primaryKey, entityClass.getName());
        return entityClass.cast(persistedEntities.findEntity(entityKey));
    }


    @Override
    public void attachEntity(Object entity)  {
        if (new LongTypeId(entity).isEntityIdNull()) {
            pendingEntities.persistEntity(entity);
            return;
        }
        persistedEntities.persistEntity(getEntityKey(entity), entity);
    }

    @Override
    public void detachEntity(Object entity) {
        pendingEntities.removeEntity(entity);
        persistedEntities.removeEntity(getEntityKey(entity));
    }

    @Override
    public Set<Object> getPendingEntities() {
        return pendingEntities.getEntities();
    }

    @Override
    public Collection<Object> getPersistedEntities() {
        return persistedEntities.getEntities();
    }

    @Override
    public void captureDatabaseSnapshot(Object entity) {
        databaseSnapshots.addDatabaseSnapshot(entity);
    }

    @Override
    public Object getDatabaseSnapshot(Object entity) {
        return databaseSnapshots.getDatabaseSnapshot(entity);
    }

    @Override
    public void reset() {
        pendingEntities.clear();
        persistedEntities.clear();
        databaseSnapshots.clear();
    }

    private EntityKey getEntityKey(Object entity) {
        return new EntityKey(
            new LongTypeId(entity).getId(),
            entity.getClass().getName()
        );
    }

}
