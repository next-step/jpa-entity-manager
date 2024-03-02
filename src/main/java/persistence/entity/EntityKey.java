package persistence.entity;

import java.util.Objects;

public class EntityKey {

    private final Class<?> clazz;
    private final Object id;

    public EntityKey(Class<?> clazz, Object id) {
        this.clazz = clazz;
        this.id = id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EntityKey entityKey = (EntityKey) object;
        return Objects.equals(clazz, entityKey.clazz) && Objects.equals(id, entityKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, id);
    }
}
