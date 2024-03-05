package persistence.core;

public interface EntityPersister {

    <T> Long insert(T entity);

    boolean update(Object entity) throws Exception;

    void delete(Object entity) throws Exception;

    void setIdentifier(Object entity, Object id) throws Exception;

}
