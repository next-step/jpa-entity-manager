package persistence.core;

public interface EntityManager {

    <T> T find(Class<T> entityClass, Object primaryKey) throws Exception;

   void persist(Object entity) throws Exception;

    void remove(Object entity) throws Exception;

}
