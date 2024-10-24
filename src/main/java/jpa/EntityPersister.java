package jpa;

public interface EntityPersister {

    void update(Object entity);

    void insert(Object object);

    void delete(Object object);
}
