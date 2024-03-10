package persistence.entity;

import java.util.Objects;

public class EntityCacheKey {

    private final Class<?> clazz;
    private final Object id;

    public EntityCacheKey(Class<?> clazz, Object id) {
        this.clazz = clazz;
        this.id = id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EntityCacheKey that = (EntityCacheKey) object;
        return Objects.equals(clazz, that.clazz) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, id);
    }
}
