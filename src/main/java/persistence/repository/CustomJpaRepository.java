package persistence.repository;

import persistence.entity.EntityManager;
import persistence.sql.metadata.EntityMetadata;

import java.util.Objects;

public class CustomJpaRepository<T, ID> {

    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T entity) {
        if (isEmptyEntity(entity)) {
            return entityManager.persist(entity);
        }

        return entityManager.merge(entity);
    }

    private boolean isEmptyEntity(T entity) {
        Class<?> clazz = entity.getClass();
        EntityMetadata entityMetadata = EntityMetadata.of(clazz, entity);
        Object key = entityMetadata.getPrimaryKey().getValue();

        return Objects.isNull(key);
    }
}
