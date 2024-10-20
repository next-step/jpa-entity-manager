package persistence.entity;

public interface EntityManager {
    <T> T find(Class<T> clazz, Object id);

    void persist(Object entity);

    void update(Object entity);

    void remove(Object entity);
}
