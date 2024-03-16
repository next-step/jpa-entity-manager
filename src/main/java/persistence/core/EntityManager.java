package persistence.core;

public interface EntityManager {

    <T> T find(Class<T> entityClass, Long primaryKey);

   void persist(Object entity);

    void remove(Object entity);

    public <T> T merge(T entity);

    void flush();

    void clear();
}
