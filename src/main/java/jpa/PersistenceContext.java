package jpa;

public interface PersistenceContext {

    void add(Object entity);

    <T> T get(Class<T> clazz, Long id);

    void remove(Object entity);

    void update(Object entity);

    <T> T getDatabaseSnapshot(T entity);

    void createDatabaseSnapshot(Object entity);
}
