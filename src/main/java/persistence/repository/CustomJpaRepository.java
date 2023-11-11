package persistence.repository;

import java.util.Optional;
import persistence.entity.EntityManager;

public class CustomJpaRepository <T, ID> {
  private final EntityManager entityManager;
  public CustomJpaRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }


  public T save(T entity){
    entityManager.persist(entity);
    return entity;
  }

  public Optional<T> findById(T entity, Long id){
    return (Optional<T>) entityManager.find(entity.getClass(), id);
  }
}
