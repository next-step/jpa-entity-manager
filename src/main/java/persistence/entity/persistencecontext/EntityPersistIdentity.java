package persistence.entity.persistencecontext;

import java.util.Objects;

public class EntityPersistIdentity {
    private final Class<?> entityClass;
    private final Object entityId;

    public EntityPersistIdentity(Class<?> entityClass, Object entityId) {
        this.entityClass = entityClass;
        this.entityId = entityId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityPersistIdentity that = (EntityPersistIdentity) o;
        return Objects.equals(entityClass, that.entityClass) && Objects.equals(entityId, that.entityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityClass, entityId);
    }
}
