package persistence.jpa;

public interface JpaRepository<T, ID> {

    T save(T entity);
}
