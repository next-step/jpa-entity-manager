package persistence.entity;

public interface EntityPersister {

  boolean update(Object entity, String fieldName);

  void insert(Object entity);

  void delete(Object entity);
}
