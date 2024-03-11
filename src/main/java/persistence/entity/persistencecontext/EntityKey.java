package persistence.entity.persistencecontext;

import java.util.Objects;

public class EntityKey {
    private final Class<?> clazz;
    private final Long id;

    public EntityKey(Class<?> clazz, Long id) {
        this.clazz = clazz;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityKey that = (EntityKey) o;
        return Objects.equals(clazz, that.clazz) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, id);
    }
}
