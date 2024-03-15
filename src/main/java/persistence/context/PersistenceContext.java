package persistence.context;

public interface PersistenceContext {
    <T> T getEntity(Class<T> clazz, Object id);

    void addEntity(Object id, Object entity);

    void removeEntity(Object entity);

    EntitySnapshot getDatabaseSnapshot(Object id, Object entity);

    EntitySnapshot getCachedDatabaseSnapshot(Object id, Object entity);
}
