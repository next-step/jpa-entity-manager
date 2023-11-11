package persistence.entity;

import persistence.sql.ddl.EntityMetadata;

import java.util.Objects;

public class CustomJpaRepository<T, ID> {

    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T t) {
        if (isNew(t)) {
            entityManager.persist(t);
            return t;
        }

        return entityManager.merge(t);
    }

    private boolean isNew(T t) {
        EntityMetadata entityMetadata = EntityMetadata.of(t.getClass());
        String idColumnValue = entityMetadata.getIdColumnValue(t);

        return Objects.isNull(idColumnValue);
    }

}
