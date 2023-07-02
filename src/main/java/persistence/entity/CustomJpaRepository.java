package persistence.entity;

import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.Arrays;

public class CustomJpaRepository<T, ID> {
    private final EntityManager entityManager;
    private final Class<T> clazz;

    public CustomJpaRepository(Class<T> clazz, EntityManager entityManager) {
        this.clazz = clazz;
        this.entityManager = entityManager;
    }

    public T save(T t) throws IllegalAccessException {
        if (entityManager.isChanged(t)) {
            Proxy proxy = Proxy.copyObject(t);
            entityManager.update(proxy.entityClass(), proxy);

            return t;
        }

        Field uniqueField = unique(t.getClass().getFields());

        if (isNew(uniqueField, t)) {
            entityManager.persist(t);
        }


        return t;
    }

    private Object uniqueValue(Field field, T object) {
        try {
            return field.get(object);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isNew(Field field, T object) {
        try {
            field.setAccessible(true);
            return String.valueOf(field.get(object)) == null;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Field unique(Field[] field) {
        return Arrays.stream(field)
                .filter(it -> it.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public T findById(ID id) {
        return entityManager.find(clazz, (Long) id);
    }
}
