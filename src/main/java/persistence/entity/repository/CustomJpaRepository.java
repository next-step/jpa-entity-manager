package persistence.entity.repository;

import persistence.entity.manager.EntityManager;

public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T t) {
        return entityManager.persist(t);
    }
}
