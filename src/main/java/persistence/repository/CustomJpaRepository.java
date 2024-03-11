package persistence.repository;

import persistence.entity.EntityManager;

public class CustomJpaRepository {
    private final EntityManager entityManager;
    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    <T> T save (T entity) {
        return (T) entityManager.persist(entity);
    }
}
