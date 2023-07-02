package persistence.entity;

public interface PersistenceContext {
    boolean hasEntity(EntityKey key);

    <T> T getEntity(EntityKey<T> key);

    void addEntity(Object entity);

    void removeEntity(Object entity);

    <T> T getDatabaseSnapshot(EntityKey<T> key, T entity);

    <T> T getCachedDatabaseSnapshot(EntityKey<T> key);
}
