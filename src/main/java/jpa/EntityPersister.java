package jpa;

public interface EntityPersister {
    <T> T find(Class<T> clazz, Object id);

    void update(Object entity);

    void insert(Object object);

    void delete(Object object);
}
