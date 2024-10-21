package persistence.entity;

public interface EntityPersister {
    void insert(Object entity);

    void update(Object entity);

    void delete(Object entity);
}
