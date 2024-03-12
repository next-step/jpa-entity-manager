package persistence.sql.entity;

public interface EntityManager {

    <T> T find(Class<T> clazz, Long Id) throws IllegalAccessException;

    Object persist(Object entity) throws IllegalAccessException;

    Object merge(Object entity) throws IllegalAccessException;

    void remove(Object entity) throws IllegalAccessException;
}
