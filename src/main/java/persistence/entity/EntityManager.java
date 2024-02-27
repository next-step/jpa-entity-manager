package persistence.entity;

public interface EntityManager {

    <T> T find(Class<T> clazz, Object id);

    <T> void persist(T entity);

    <T> void remove(T entity);

    <T> boolean update(T entity, Object id);
}
