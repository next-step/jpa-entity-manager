package persistence.entity;

import java.util.Objects;

public class EntityId {

    private final Object id;

    public EntityId(Object id) {
        this.id = id;
    }

    public Object value() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityId id1 = (EntityId) o;

        return Objects.equals(id, id1.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }
}
