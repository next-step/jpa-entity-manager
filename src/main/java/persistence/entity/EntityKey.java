package persistence.entity;

import persistence.sql.mapping.ColumnData;
import persistence.sql.mapping.Columns;

import java.util.Objects;

public class EntityKey {
    private final Class<?> entityType;
    private final Object id;

    public EntityKey(Class<?> entityType, Object id) {
        this.entityType = entityType;
        this.id = id;
    }

    public static EntityKey fromEntity(Object entity){
        Columns columns = Columns.createColumnsWithValue(entity);
        ColumnData keyColumn = columns.getKeyColumn();
        return new EntityKey(entity.getClass(), keyColumn.getValue());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EntityKey entityKey = (EntityKey) o;

        if (!Objects.equals(entityType, entityKey.entityType)) return false;
        return Objects.equals(id, entityKey.id);
    }

    public boolean hasId() {
        return id != null;
    }

    @Override
    public int hashCode() {
        int result = entityType != null ? entityType.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("EntityKey(%s, %s)", entityType.getSimpleName(), id);
    }
}
