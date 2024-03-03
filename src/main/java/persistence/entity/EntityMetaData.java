package persistence.entity;

import persistence.sql.column.Columns;
import persistence.sql.dialect.Dialect;

import java.util.Objects;

public class EntityMetaData {
    private final Class<?> clazz;
    private final Columns columns;

    public EntityMetaData(Object entity, Dialect dialect) {
        this.clazz = entity.getClass();
        this.columns = new Columns(entity, dialect);
    }

    public EntityMetaData(Class<?> clazz, Columns columns) {
        this.clazz = clazz;
        this.columns = columns;
    }

    public boolean isDirty(EntityMetaData entityMetaData) {
        return columns.isDirty(entityMetaData.columns);
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EntityMetaData that = (EntityMetaData) object;
        return Objects.equals(clazz, that.clazz) && Objects.equals(columns, that.columns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, columns);
    }
}
