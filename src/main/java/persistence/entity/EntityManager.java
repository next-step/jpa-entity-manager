package persistence.entity;

public interface EntityManager {
    <T> T find(Class<T> clazz, Long key);

    void persist(Object entity) throws IllegalAccessException;

    void remove(Object entity) throws IllegalAccessException;

    boolean contains(Object entity) throws IllegalAccessException;
}
