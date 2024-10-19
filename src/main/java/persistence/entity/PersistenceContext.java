package persistence.entity;

public interface PersistenceContext {
    void addEntity(Object entry);

    <T> T getEntity(Class<T> entityType, Object id);

    <T> void removeEntity(Class<T> entityType, Object id);
}
