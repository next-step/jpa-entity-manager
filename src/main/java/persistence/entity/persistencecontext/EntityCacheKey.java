package persistence.entity.persistencecontext;

import java.util.Objects;

public class EntityCacheKey {
    private final Class<?> clazz;
    private final Long id;

    public EntityCacheKey(Class<?> clazz, Long id) {
        this.clazz = clazz;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityCacheKey that = (EntityCacheKey) o;
        return Objects.equals(clazz, that.clazz) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, id);
    }
}
