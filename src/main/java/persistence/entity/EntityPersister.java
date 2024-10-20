package persistence.entity;

public interface EntityPersister {
    <T> T find(Class<T> entityType, Object id);

    void insert(Object entity);

    void update(Object entity);

    void delete(Object entity);
}
