package persistence.entity;

public interface EntityManager {
    <T> T find(Class<T> clazz, Object Id);

    void persist(Object entity);

    void remove(Object entity);

    <T> T merge(T entity);

    void flush();
}
