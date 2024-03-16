package persistence.sql;

import java.util.Objects;
import persistence.exception.UnsupportedClassException;

public class EntityId {
    private final Class<?> entityClass;
    private final Object id;

    public EntityId(Class<?> entityClass, Object id) {
        this.entityClass = entityClass;
        this.id = id;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public Object getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EntityId entityId = (EntityId) o;
        return Objects.equals(entityClass, entityId.entityClass) && Objects.equals(
            id, entityId.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityClass, id);
    }

    public String queryString() {
        // TODO: remove this else-if statement
        if (id.getClass().equals(Boolean.class)) {
            return id == Boolean.TRUE ? "1" : "0";
        } else if (id.getClass().equals(String.class)) {
            return String.format("'%s'", id);
        } else if (id.getClass().equals(Integer.class)) {
            return id.toString();
        } else if (id.getClass().equals(Long.class)) {
            return id.toString();
        }

        throw new UnsupportedClassException(id.getClass());
    }
}
