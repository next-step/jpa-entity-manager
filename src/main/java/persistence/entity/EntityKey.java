package persistence.entity;

import java.util.Objects;


public class EntityKey {
    private final Object id;
    private final Class<?> entityClass;

    public EntityKey(Object id, Class<?> entityClass) {
        this.id = id;
        this.entityClass = entityClass;
    }

    public Object getId() {
        return id;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityKey entityKey = (EntityKey) o;
        return Objects.equals(id, entityKey.id) && Objects.equals(entityClass, entityKey.entityClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entityClass);
    }
}