package persistence.entity;

public interface EntityPersister {
    boolean update(Object entity);

    void insert(Object entity);

    void delete(Object entity);
}
