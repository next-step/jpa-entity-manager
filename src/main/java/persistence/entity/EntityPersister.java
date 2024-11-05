package persistence.entity;

public interface EntityPersister {
    void update(Object entity);

    void insert(Object entity);

    void delete(Object entity);
}

