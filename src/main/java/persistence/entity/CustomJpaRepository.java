package persistence.entity;

import persistence.sql.ddl.domain.Columns;
import persistence.sql.dml.domain.Value;

public class CustomJpaRepository<T, ID> implements JpaRepository<T, ID> {

    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public T save(T entity) {
        if (isNew(entity)) {
            return entityManager.persist(entity);
        }
        return entityManager.merge(entity);
    }

    private boolean isNew(T entity) {
        Columns columns = new Columns(entity.getClass());
        Object id = new Value(columns.getPrimaryKeyColumn(), entity).getOriginValue();
        return id == null;
    }

}
