package persistence.entity;

import java.io.Serializable;
import java.util.Objects;

public class EntityKey {
    private final Serializable id;
    private final Class<?> entityClass;

    public EntityKey(Serializable id, Class<?> entityClass) {
        this.id = id;
        this.entityClass = entityClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityKey entityKey = (EntityKey) o;

        return id.equals(entityKey.id)
                && entityClass.equals(entityKey.entityClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entityClass);
    }

    public Serializable getId() {
        return id;
    }

}
