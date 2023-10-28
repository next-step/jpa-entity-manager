package persistence.entity.repository;

import persistence.entity.attribute.EntityAttribute;
import persistence.entity.manager.EntityManager;

import java.lang.reflect.Field;
import java.util.Optional;

public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;

    public CustomJpaRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public T save(T t) {
        EntityAttribute entityAttribute = EntityAttribute.of(t.getClass());
        Field idField = entityAttribute.getIdAttribute().getField();
        idField.setAccessible(true);
        try {
            String id = Optional.ofNullable(idField.get(t)).map(String::valueOf).orElse(null);
            T snapshot = entityManager.getCachedDatabaseSnapshot((Class<T>) t.getClass(), id);

            return entityManager.persist(t);

        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }
}
