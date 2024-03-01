package persistence.entity;

public interface EntityPersister {

    boolean update(Object entity, Object id);

    <T> void insert(T entity);

    void delete(Object entity, Object id);
}
