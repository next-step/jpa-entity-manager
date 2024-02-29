package persistence.entity;

import persistence.sql.meta.Table;

public class CustomJpaRepository<T, ID> implements Repository<T, ID> {

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
        Table table = Table.getInstance(entity.getClass());
        return table.getIdValue(entity) == null;
    }
}
