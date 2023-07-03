package persistence.entity;

public interface EntityManager {

    <T> T find(Class<T> entityClass, Long primaryKey);

    void persist(Object entity);

    void remove(Object entity);

    boolean isNew(Object entity);

    <T> T merge(T entity);
}
