package persistence.entity;

public interface EntityPersister {

    boolean update(Object entity, Object id);

    void insert(Object entity);

    void delete(Object entity, Object id);
}
