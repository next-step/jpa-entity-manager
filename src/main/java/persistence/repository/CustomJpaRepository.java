package persistence.repository;

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

  public T findById(T entity, Long id){
    return (T) entityManager.find(entity.getClass(), id);
  }
}
