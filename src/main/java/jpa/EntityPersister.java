package jpa;

public interface EntityPersister {

    void update(Object entity);

    <T> T insert(T entity);

    void delete(Object object);
}
