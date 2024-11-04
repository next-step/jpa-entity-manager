package persistence.entity;

public interface EntityPersister {

    <T> void insert(T entity);
    <T> void update(T entity);
    <T> void delete(T entity);

}
