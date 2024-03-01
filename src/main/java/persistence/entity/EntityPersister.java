package persistence.entity;

public interface EntityPersister {

    boolean update(Object entity);

    Object insert(Object entity);

    void delete(Object entity);
}
