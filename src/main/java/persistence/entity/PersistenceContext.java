package persistence.entity;

public interface PersistenceContext {
    <T> T getEntity(Class<T> entityClass, Long id);

    void addEntity(Object entityObject);

    void removeEntity(Object entityObject);
}
