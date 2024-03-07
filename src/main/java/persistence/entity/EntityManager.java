package persistence.entity;

public interface EntityManager {

    <T> T find(final Class<T> clazz, final Object key);

    void persist(final Object entity);

    void remove(final Object entity);

}
