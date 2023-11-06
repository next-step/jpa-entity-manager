package repository;

import java.util.Optional;
import persistence.entity.EntityManager;
import persistence.sql.dialect.ColumnType;
import persistence.sql.schema.EntityClassMappingMeta;
import persistence.sql.schema.EntityObjectMappingMeta;

public class CustomJpaRepository<T, ID> implements JpaRepository<T, ID> {

    private final EntityManager entityManager;
    private final Class<T> clazz;

    public CustomJpaRepository(EntityManager entityManager, Class<T> clazz) {
        this.entityManager = entityManager;
        this.clazz = clazz;
    }

    public T save(T t) {
        if (t == null) {
            throw new RuntimeException("Entity cannot be null");
        }

        if (EntityObjectMappingMeta.isNew(t)) {
            return clazz.cast(entityManager.persist(t));
        }

        return entityManager.merge(clazz, t);
    }

    @Override
    public Optional<T> findById(ID id) {
        if (id == null) {
            throw new RuntimeException("id cannot be null");
        }

        return Optional.ofNullable(entityManager.find(clazz, id));
    }
}
