package persistence.entity;

import java.io.Serializable;

public class EntityEntry {
    private Status status;
    private Serializable id;
    private final PersistenceContext persistenceContext;

    private EntityEntry(Status status, Serializable id, PersistenceContext persistenceContext) {
        this.status = status;
        this.id = id;
        this.persistenceContext = persistenceContext;
    }

    public static EntityEntry inSaving(PersistenceContext persistenceContext) {
        return new EntityEntry(Status.SAVING, 0L, persistenceContext);
    }

    public static EntityEntry deleted(Serializable id, PersistenceContext persistenceContext) {
        return new EntityEntry(Status.DELETED, id, persistenceContext);
    }

    public static EntityEntry managed(Serializable id, PersistenceContext persistenceContext) {
        return new EntityEntry(Status.MANAGED, id, persistenceContext);
    }

    public static EntityEntry loading(Serializable id, PersistenceContext persistenceContext) {
        return new EntityEntry(Status.LOADING, id, persistenceContext);
    }

    public boolean isManaged() {
        return status.isManaged();
    }

    public boolean isNotSaving() {
        return status != Status.SAVING;
    }

    public void addEntity(EntityKey entityKey, Object entity) {
        persistenceContext.addEntity(entityKey, entity);
    }

    public void addDatabaseSnapshot(EntityKey entityKey, Object entity) {
        persistenceContext.addDatabaseSnapshot(entityKey, entity);
    }

    public void removeEntity(EntityKey entityKey) {
        persistenceContext.removeEntity(entityKey);
    }

    public void updateStatus(Status status) {
        if (this.status.isValidStatusTransition(status)) {
            this.status = status;
            return;
        }

        throw new IllegalArgumentException("Invalid status transition from: " + this.status + " to: " + status);
    }

    public boolean hasDirtyColumns(EntityKey entityKey) {
        final EntitySnapshot entitySnapshot = persistenceContext.getDatabaseSnapshot(entityKey);
        final Object managedEntity = persistenceContext.getEntity(entityKey);
        return entitySnapshot.hasDirtyColumns(entitySnapshot, managedEntity);
    }

    public Object getEntity(EntityKey entityKey) {
        return persistenceContext.getEntity(entityKey);
    }

    public Serializable getId() {
        return id;
    }
}
