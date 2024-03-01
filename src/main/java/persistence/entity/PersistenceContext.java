package persistence.entity;

public interface PersistenceContext {

    <T> T getEntity(Class<T> clazz, Object id);

    void addEntity(Object entity);

    void removeEntity(Object entity);

    void updateCache(Object entity);

    boolean isNotDirty(Object entity, Object id);
}

