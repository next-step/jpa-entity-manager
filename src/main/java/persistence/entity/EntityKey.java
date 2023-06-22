package persistence.entity;

import jakarta.persistence.Id;
import persistence.sql.exception.IllegalFieldAccessException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

public class EntityKey<T> {
    private final Class<T> clazz;
    private final Object id;
    private final int idHash;

    public EntityKey(Class<T> clazz, Object id) {
        this.clazz = clazz;
        this.id = id;
        this.idHash = Objects.hash(id);
    }

    public EntityKey(T object) {
        this.clazz = (Class<T>) object.getClass();
        Field field = Arrays.stream(object.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(Id.class))
                .findAny()
                .get();
        field.setAccessible(true);
        try {
            this.id = field.get(object);
        } catch (IllegalAccessException e) {
            throw new IllegalFieldAccessException(e);
        }
        this.idHash = Objects.hash(id);
    }

    public Class<T> getEntityClass() {
        return clazz;
    }

    public Object getEntityId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityKey<?> entityKey = (EntityKey<?>) o;
        return idHash == entityKey.idHash && Objects.equals(clazz, entityKey.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, idHash);
    }
}
