package persistence.entity;

import java.util.Objects;

public class EntityKey {
    private final Class<?> entityClass;

    private final Object pk;

    public EntityKey(Class<?> entityClass, Object pk) {
        this.entityClass = entityClass;
        this.pk = pk;
    }

    @Override
    public boolean equals(Object comparingObject) {
        if (this == comparingObject) {
            return true;
        };
        if (comparingObject == null || getClass() != comparingObject.getClass()) {
            return false;
        }

        EntityKey key = (EntityKey) comparingObject;

        return key.entityClass.equals(entityClass)
                && key.pk.equals(pk);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityClass, pk);
    }
}
