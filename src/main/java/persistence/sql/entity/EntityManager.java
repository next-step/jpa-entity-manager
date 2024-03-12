package persistence.sql.entity;

public interface EntityManager {

    <T> T find(Class<T> clazz, Long Id);

    Object persist(Object entity) throws IllegalAccessException;

    Object merge(Object entity) throws IllegalAccessException;

    void remove(Object entity) throws IllegalAccessException;
}
