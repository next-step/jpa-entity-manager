package repository;

import persistence.entity.EntityManager;

public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T t) {
        return (T) entityManager.persist(t);
    }
}
