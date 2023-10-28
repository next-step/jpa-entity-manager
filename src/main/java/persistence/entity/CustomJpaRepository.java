package persistence.entity;

import persistence.core.EntityMetadataProvider;
import persistence.util.ReflectionUtils;

import java.util.Objects;

public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;
    private final Class<T> clazz;

    public CustomJpaRepository(final EntityManager entityManager, final Class<T> clazz) {
        this.entityManager = entityManager;
        this.clazz = clazz;
    }

    public T save(final T t) {
        if (isNew(t)) {
            entityManager.persist(t);
            return t;
        }

        return entityManager.merge(t);
    }

    public T findById(final ID id) {
        return entityManager.find(clazz, id);
    }

    private boolean isNew(final T t) {
        final String idColumnFieldName = EntityMetadataProvider.getInstance()
                .getEntityMetadata(t.getClass())
                .getIdColumnFieldName();
        final Object idValue = ReflectionUtils.getFieldValue(t, idColumnFieldName);

        return Objects.isNull(idValue);
    }
}
