package persistence.entity;

public interface PersistenceContext {

    <T> T find(Class<T> entityClass, Object primaryKey);

    void persist(Object entity);

    void remove(Object entity);

    void update(Object entity) throws IllegalAccessException;

    void flush() throws IllegalAccessException;

}
