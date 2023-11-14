package persistence.entity;

import java.util.List;
import java.util.Optional;

public interface PersistenceContext {

  Optional<Object> getEntity(Long id, Class<?> clazz);

  <T> void addEntity(Long id, T entity);

  <T> void removeEntity(T entity);

  <T> void putDatabaseSnapshot(Long id, T entity);

  <T> boolean isSameWithSnapshot(Long id, T entity);

  List<Object> dirtyCheckedEntities();
  
}