package persistence.entity;

import persistence.sql.ddl.domain.Column;
import persistence.sql.ddl.domain.Columns;

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

    public boolean isNew(T entity) {
        Columns columns = new Columns(entity.getClass());
        Column primaryKeyColumn = columns.getPrimaryKeyColumn();

        Object id = columns.getOriginValue(entity);
        Class<?> idType = primaryKeyColumn.getField().getType();

        if (!idType.isPrimitive()) {
            return id == null;
        } else if (id instanceof Number) {
            return ((Number) id).longValue() == 0L;
        } else {
            throw new IllegalArgumentException(String.format("Unsupported primitive id type %s", idType));
        }
    }

}
