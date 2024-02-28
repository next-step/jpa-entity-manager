package persistence.entity;

public interface EntityManager {
    <T> T find(Class<T> clazz, Long id);

    <T> T persist(T entity);

    void remove(Object entity);

    <T> T merge(T entity);
}
