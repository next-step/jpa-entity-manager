package persistence.entity;

public interface EntityManager {

    <T> T find(final Class<T> clazz, final Object key);

    <T> T persist(final T entity);

    <T> T merge(final T entity);

    void remove(final Object entity);

}
