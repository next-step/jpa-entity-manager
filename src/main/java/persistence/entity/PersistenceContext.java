package persistence.entity;

public interface PersistenceContext {
    <T> T getEntity(Class<T> entityClass, Object id);

    void addEntity(Object entityObject) throws NoSuchFieldException, IllegalAccessException;

    void removeEntity(Object entityObject);
}
