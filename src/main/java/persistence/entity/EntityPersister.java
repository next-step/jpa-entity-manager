package persistence.entity;

import java.util.Optional;

public interface EntityPersister<T> {

  boolean update(T entity);

  Long insert(T entity);

  void delete(T entity);

  Optional<T> load(Long id);

  boolean entityExists(Object entity);

  Optional<Long> getEntityId(Object entity);
}
