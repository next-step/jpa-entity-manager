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
        Object id = table.getIdValue(entity);
        if (id == null) {
            return true;
        }
        if (id instanceof Number) {
            return ((Number) id).longValue() == 0L;
        }
        throw new IllegalArgumentException(String.format("Unsupported id type %s", id.getClass()));
    }
}
