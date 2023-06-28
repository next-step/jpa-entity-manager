package persistence.repository;

import jakarta.persistence.Id;
import persistence.common.Fields;
import persistence.entity.EntityManager;

public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T entity) {
        if (isNewEntity(entity)) {
            entityManager.persist(entity);
            return entity;
        }

        return entityManager.merge(entity);
    }

    private boolean isNewEntity(T entity) {
        Object id = Fields.of(entity.getClass())
                .getAccessibleField(Id.class)
                .getValue(entity);

        return id == null;
    }
}
