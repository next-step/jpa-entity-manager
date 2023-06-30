package persistence.entity;

import java.util.Objects;

public class EntityKey {
    private final Object id;
    private final String name;

    private EntityKey(Object key, String entityName) {
        this.id = key;
        this.name = entityName;
    }

    public static EntityKey of(Object key, String entityName) {
        return new EntityKey(key, entityName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityKey entityKey = (EntityKey) o;
        return Objects.equals(id, entityKey.id) && Objects.equals(name, entityKey.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
