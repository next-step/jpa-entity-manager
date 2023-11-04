package repository;

import java.util.List;
import persistence.entity.EntityManager;

public class SimpleCrudRepository<T, ID> implements CrudRepository<T, ID> {
    private final EntityManager entityManager;
    private final Class<T> tClass;

    public SimpleCrudRepository(EntityManager entityManager, Class<T> tClass) {
        this.entityManager = entityManager;
        this.tClass = tClass;
    }

    public T save(T entity) {
        return (T) entityManager.persist(entity);
    }

    public void delete(T entity) {
        entityManager.remove(entity);
    }
    public void flush() {
        entityManager.flush();
    }

    public T findById(Class<T> tClass, ID id) {
        return entityManager.find(tClass, id);
    }

    public List<T> findAll() {
        return entityManager.findAll(tClass);
    }
}
