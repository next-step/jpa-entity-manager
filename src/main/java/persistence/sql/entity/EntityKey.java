package persistence.sql.entity;

import java.util.Objects;

public class EntityKey {
    private final Object id;
    private final Class<?> entityType;

    public EntityKey(Object id, Class<?> entityType) {
        this.id = id;
        this.entityType = entityType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityKey entityKey = (EntityKey) o;
        return Objects.equals(id, entityKey.id) && Objects.equals(entityType, entityKey.entityType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entityType);
    }
}
