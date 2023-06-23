package persistence.entity;

import java.util.Objects;

public class EntityKey {
    private final Long key;
    private final String entityName;

    private EntityKey(Long key, String entityName) {
        this.key = key;
        this.entityName = entityName;
    }

    public static EntityKey of(Long key, String entityName) {
        return new EntityKey(key, entityName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityKey entityKey = (EntityKey) o;
        return Objects.equals(key, entityKey.key) && Objects.equals(entityName, entityKey.entityName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, entityName);
    }
}
