package persistence.entity;

public interface PersistenceContext {
    void addEntity(Object entry);

    <T> T getEntity(Class<T> entityType, Object id);

    void removeEntity(Object entity);

    <T> T getSnapshot(Class<T> entityType, Object id);
}
