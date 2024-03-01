package repository;

import persistence.entity.EntityManager;
import persistence.entity.EntityMeta;

public class CustomJpaRepository<T, ID> implements JpaRepository<T, ID> {
    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T entity) {
        EntityMeta entityMeta = EntityMeta.from(entity);
        if (entityMeta.isNew(entity)) {
            return entityManager.persist(entity);
        }
        return entityManager.merge(entity);
    }
}
