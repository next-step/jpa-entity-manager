package persistence.entity;

public interface PersistenceContext {
    <T> T getEntity(Class<T> entityClass, Object id);

    void addEntity(Object entityObject);

    void removeEntity(Object entityObject);
}
