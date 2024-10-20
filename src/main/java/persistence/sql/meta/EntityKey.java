package persistence.sql.meta;

import java.util.Objects;

public class EntityKey {
    private final Class<?> entityType;
    private final Object id;

    public EntityKey(Class<?> entityType, Object id) {
        this.entityType = entityType;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityKey entityKey = (EntityKey) o;
        return Objects.equals(entityType, entityKey.entityType) && Objects.equals(id, entityKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityType, id);
    }
}
