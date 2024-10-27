package persistence.entity;

public interface EntityPersister {

    <T> void insert(T entity, EntityManager entityManager);
    <T> void update(T entity, EntityManager entityManager);
    <T> void delete(T entity, EntityManager entityManager);

}
