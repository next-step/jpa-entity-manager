package persistence;

import java.util.Objects;

public class EntityInfo<T> {

    private final Object id;
    private final Class<T> clazz;

    public EntityInfo(Object id, Class<T> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public Object getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof EntityInfo<?> other)) return false;
        return Objects.equals(id, other.id) && clazz.equals(other.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clazz);
    }
}
