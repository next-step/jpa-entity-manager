package hibernate.entity.persistencecontext;

import hibernate.entity.meta.EntityClass;

import java.util.Objects;

public class EntityKey {

    private final Object id;
    private final EntityClass<?> entityClass;

    public EntityKey(final Object id, final Class<?> clazz) {
        this.id = id;
        this.entityClass = EntityClass.getInstance(clazz);
    }

    public EntityKey(final Object id, final Object object) {
        this(id, object.getClass());
    }

    @Override
    public boolean equals(final Object entity) {
        if (this == entity) return true;
        if (entity == null || getClass() != entity.getClass()) return false;
        EntityKey entityKey = (EntityKey) entity;
        return Objects.equals(id, entityKey.id) && Objects.equals(entityClass, entityKey.entityClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, entityClass);
    }
}
