package persistence.core;

public interface EntityPersister {

    <T> Long insert(T entity);

    boolean update(Object entity);

    void delete(Object entity);

    void setIdentifier(Object entity, Object id);

    Long getIdentifier(Object entity);

}
