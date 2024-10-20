package persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class EntityKey {
    private final Serializable id;
    private final String entityName;

    public EntityKey(Serializable id, String entityName) {
        this.id = id;
        this.entityName = entityName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityKey entityKey = (EntityKey) o;

        return id.equals(entityKey.id)
                && entityKey.entityName.equals(entityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entityName);
    }

    public Serializable getId() {
        return id;
    }

}
