package repository;

import java.util.List;

public interface CrudRepository<T, ID> {
    T save(T entity);

    void delete(T entity);

    T findById(Class<T> tClass, ID id);

    List<T> findAll();
}
