package persistence.entity;

public interface EntityManager extends AutoCloseable {

    <T> T find(Class<T> clazz, Object Id);

    Object persist(Object entity);

    void remove(Object entity);

    void clear();

    <T> T merge(Class<T> clazz, T t);
}
