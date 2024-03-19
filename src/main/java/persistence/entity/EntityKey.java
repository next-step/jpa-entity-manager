package persistence.entity;

import java.util.Objects;

public class EntityKey {
    private final Class<?> clazz;
    private final Object id;

    private EntityKey(Class<?> clazz, Object id) {
        this.clazz = clazz;
        this.id = id;
    }

    public static EntityKey of(Class<?> clazz, Object id) {
        return new EntityKey(clazz, id);
    }

    public Class<?> getClazz() {
        return this.clazz;
    }

    public Object getValue() {
        return id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EntityKey key = (EntityKey) object;
        return Objects.equals(clazz, key.clazz) && Objects.equals(id, key.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, id);
    }
}
