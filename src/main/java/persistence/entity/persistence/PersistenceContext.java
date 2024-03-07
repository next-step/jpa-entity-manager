package persistence.entity.persistence;

public interface PersistenceContext {

    <T> T getEntity(Class<T> clazz, Object id);

    void addEntity(Object id, Object entity);

    void removeEntity(Object entity);

}
