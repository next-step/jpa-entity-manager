package persistence.entity;

import java.util.Objects;
import persistence.meta.EntityMeta;

public class EntityKey {
    private final Class<?> clazz;
    private final Object id;

    private EntityKey(Class<?> clazz, Object id) {
        this.clazz = clazz;
        this.id = id;
    }

    public static EntityKey of(Class<?> clazz, Object id) {
        return new EntityKey(clazz, id);
    }
    public static EntityKey of(Object entity) {
        final Class<?> clazz = entity.getClass();
        final Object id = EntityMeta.from(clazz).getPkValue(entity);

        return new EntityKey(clazz, id);
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object getId() {
        return id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof EntityKey)) {
            return false;
        }
        EntityKey entityKey = (EntityKey) object;
        return Objects.equals(clazz, entityKey.clazz) && Objects.equals(id, entityKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, id);
    }
}
