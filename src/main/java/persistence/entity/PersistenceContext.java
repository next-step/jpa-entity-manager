package persistence.entity;

public interface PersistenceContext {

    <T> T getEntity(Class<T> clazz, Object id);

    void addEntity(Object id, Object entity);

    void removeEntity(Object entity);

    boolean isCached(Object entity);

    Object getDatabaseSnapshot(Object id, Object entity);
}
