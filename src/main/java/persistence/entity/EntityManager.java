package persistence.entity;

public interface EntityManager {
    <T> T find(Class<T> clazz, Long key);

    void persist(Object entity) throws IllegalAccessException;

    void persist(Class<?> clazz, Object entity) throws IllegalAccessException;

    void remove(Object entity) throws IllegalAccessException;

    boolean contains(Object entity) throws IllegalAccessException;

    boolean isChanged(Object entity) throws IllegalAccessException;

    void update(Class<?> clazz, Proxy entity) throws IllegalAccessException;
}
