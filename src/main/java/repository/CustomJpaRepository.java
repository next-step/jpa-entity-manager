package repository;

import persistence.entity.EntityManager;
import persistence.sql.domain.IdColumn;
import persistence.sql.domain.Table;
import utils.ValueExtractor;

public class CustomJpaRepository<T, ID> implements JpaRepository<T, ID> {
    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T entity) {
        if (isNew(entity)) {
            return entityManager.persist(entity);
        }
        return entityManager.merge(entity);
    }

    private boolean isNew(T entity) {
        Table table = Table.from(entity.getClass());
        IdColumn idColumn = table.getIdColumn();
        Object id = ValueExtractor.extract(entity, idColumn);
        return id == null;
    }
}
