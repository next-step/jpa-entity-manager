package persistence.entity;

import persistence.sql.dml.ColumnValues;
import persistence.sql.util.StringConstant;

import java.util.Objects;

public class EntityKey {

    private final Class<?> clazz;
    private final Object entityId;

    private EntityKey(Class<?> clazz, Object entityId) {
        this.clazz = clazz;
        this.entityId = entityId;
    }

    public static EntityKey of(Class<?> clazz, Object entityId) {
        return new EntityKey(clazz, entityId.toString());
    }

    public static EntityKey from(Object entity) {
        ColumnValues idValues = ColumnValues.ofId(entity);
        String entityId = String.join(StringConstant.EMPTY_STRING, idValues.values());
        return new EntityKey(entity.getClass(), entityId);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EntityKey entityKey = (EntityKey) object;
        return Objects.equals(clazz, entityKey.clazz) && Objects.equals(entityId, entityKey.entityId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, entityId);
    }
}
