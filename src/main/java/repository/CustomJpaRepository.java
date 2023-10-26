package repository;

import hibernate.entity.EntityManager;

public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T t) {
        entityManager.persist(t);
        return t;
    }
}
