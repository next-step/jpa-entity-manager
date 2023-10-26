package repository;

import hibernate.entity.EntityManager;
import hibernate.entity.meta.EntityClass;

public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T t) {
        Object entityId = EntityClass.getInstance(t.getClass())
                .getEntityId()
                .getFieldValue(t);
        if (entityId != null) {
            entityManager.merge(t);
            return t;
        }
        entityManager.persist(t);
        return t;
    }
}
