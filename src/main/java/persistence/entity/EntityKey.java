package persistence.entity;

import java.io.Serializable;

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
        int result = id.hashCode();
        result = 31 * result + entityName.hashCode();
        return result;
    }

    public Serializable getId() {
        return id;
    }

}
