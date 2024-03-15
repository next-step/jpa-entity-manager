package persistence.entity;

public interface PersistenceContext {
    <T> T getEntity(Class<T> entityClass, Object id);

    void addEntity(Object id, Object entity);

    <T> void removeEntity(T entity);

    <T> T getDatabaseSnapshot(Object id, T entity);
}
