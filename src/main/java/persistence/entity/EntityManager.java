package persistence.entity;

public interface EntityManager {

    <T> T find(Class<T> clazz, Long Id);

    <T> T persist(T entity);

    <T> T merge(T entity);

    <T> void remove(T entity);

}
