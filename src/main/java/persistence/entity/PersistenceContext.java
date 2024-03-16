package persistence.entity;

public interface PersistenceContext {
    <T> T getEntity(Class<T> entityClass, Object id);

    <T> void addEntity(Object id, T entity);

    <T> void removeEntity(T entity);

    <T> T getDatabaseSnapshot(Object id, T entity);
}
