package persistence.entity;

import java.util.Objects;

public class EntityKey {

    private final Class<?> clazz;

    private final Object id;

    public EntityKey(Class<?> clazz, Object id) {
        this.clazz = clazz;
        this.id = id;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityKey key = (EntityKey) o;

        if (!Objects.equals(clazz, key.clazz)) return false;
        return Objects.equals(id, key.id);
    }

    @Override
    public int hashCode() {
        int result = clazz != null ? clazz.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }
}
