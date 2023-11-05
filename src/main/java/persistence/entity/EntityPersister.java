package persistence.entity;

public interface EntityPersister<T> {

  boolean update(T entity);

  void insert(T entity);

  void delete(T entity);

  T load(Long id);

  boolean entityExists(Object entity);
}
