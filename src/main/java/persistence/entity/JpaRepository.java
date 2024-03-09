package persistence.entity;

public interface JpaRepository<T, ID> {

    T save(T entity);
}
