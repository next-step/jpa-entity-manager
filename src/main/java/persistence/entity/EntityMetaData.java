package persistence.entity;

import persistence.sql.column.Columns;
import persistence.sql.dialect.Dialect;

import java.util.Objects;

public class EntityMetaData {
    private final Class<?> clazz;
    private final Columns values;

    public EntityMetaData(Object entity, Dialect dialect) {
        this.clazz = entity.getClass();
        this.values = new Columns(entity, dialect);
    }

    public EntityMetaData(Class<?> clazz, Columns values) {
        this.clazz = clazz;
        this.values = values;
    }

    public boolean isDirty(EntityMetaData entityMetaData) {
        return values.isDirty(entityMetaData.values);
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EntityMetaData that = (EntityMetaData) object;
        return Objects.equals(clazz, that.clazz) && Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazz, values);
    }
}
