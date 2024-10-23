package persistence.entity;

public interface EntityManager {
    <T> T findById(Class<T> clazz, Object Id);

    void persist(Object entity);

    void remove(Object entity);

    void merge(Object entity);
}
