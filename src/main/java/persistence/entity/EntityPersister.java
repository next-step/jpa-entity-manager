package persistence.entity;

public interface EntityPersister {
    void insert(Object entity);

    void update(Object entity, Object snapshot);

    void delete(Object entity);
}
