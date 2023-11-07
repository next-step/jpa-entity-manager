package repository;

import persistence.entity.entitymanager.EntityManager;
import persistence.sql.entitymetadata.model.EntityTable;

public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T entity) {
        if (isNew(entity)) {
            entityManager.persist(entity);
        } else {
            entityManager.merge(entity);
        }

        return entity;
    }

    private boolean isNew(T entity) {
        EntityTable entityTable = new EntityTable(entity.getClass());

        return !entityTable.isExistsId(entity);
    }
}
