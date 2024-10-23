package persistence.entity;

import java.util.Objects;

public record EntityKey(Object object, Class<?> entityType) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntityKey)) {
            return false;
        }
        EntityKey entityKey = (EntityKey) o;
        return Objects.equals(object, entityKey.object) && Objects.equals(entityType, entityKey.entityType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(object, entityType);
    }
}
