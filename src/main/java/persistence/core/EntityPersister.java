package persistence.core;

public interface EntityPersister {

    <T> void insert(T entity);
    <T> T select(Class<?> clazz, Long id) throws Exception;
    boolean update(Object entity) throws Exception;
    void delete(Object entity) throws Exception;

}
