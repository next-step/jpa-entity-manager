package persistence.persistencecontext;

import java.util.Objects;

public class EntityKey {
    private final Object id;
    private final Class<?> clazz;

    public EntityKey(Object id, Class<?> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EntityKey entityKey = (EntityKey) object;
        return Objects.equals(id, entityKey.id) && Objects.equals(clazz, entityKey.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clazz);
    }
}
