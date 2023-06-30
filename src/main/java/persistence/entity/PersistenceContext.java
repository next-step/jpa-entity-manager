package persistence.entity;

public interface PersistenceContext {
    Object NO_ROW = new SnapshotObject("NO_ROW");

    Object removeEntity(EntityKey key);

    Object getEntity(EntityKey key);

    boolean containsEntity(EntityKey key);

    void addEntity(EntityKey key, Object entity);

    Object getDatabaseSnapshot(Object id, Object entity);

    Object getCachedDatabaseSnapshot(EntityKey id);
}
