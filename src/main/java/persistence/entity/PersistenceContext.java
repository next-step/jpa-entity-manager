package persistence.entity;

public interface PersistenceContext {

    <T> T getEntity(Class<T> clazz, Long Id);

    void addEntity(Object entity);

    void removeEntity(Object entity);
}
