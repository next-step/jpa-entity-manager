package repository;

import java.util.Optional;

public interface JpaRepository<T, ID> {

    T save(T t);

    Optional<T> findById(ID id);
}
