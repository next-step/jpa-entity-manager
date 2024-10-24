package orm;

public interface EntityManager {

    <T> T find(Class<T> clazz, Object id);

    <T> T persist(T entity);

    void remove(Object entity);

    <T> T update(T entity);
}
