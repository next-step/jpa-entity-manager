package persistence.jpa;

import persistence.entity.EntityManager;
import persistence.sql.column.IdColumn;


public class CustomJpaRepository<T, ID> implements JpaRepository<T, ID> {

    private final EntityManager entityManager;


    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }


    @Override
    public T save(T entity) {
        if(isNew(entity)) {
            return entityManager.persist(entity);
        }
        return entityManager.merge(entity);
    }

    private boolean isNew(T entity) {
        IdColumn idColumn = new IdColumn(entity, entityManager.getDialect());
        return idColumn.isNull();
    }
}
