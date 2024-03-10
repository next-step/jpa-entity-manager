package persistence.core;

public interface EntityManager {

    <T> T find(Class<T> entityClass, Object primaryKey);

   void persist(Object entity);

    void remove(Object entity);

    public void flush();

}
