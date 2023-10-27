package hibernate.entity.meta;

import jakarta.persistence.Table;

import java.util.Objects;

public class EntityTableName {

    private final String value;

    public EntityTableName(final Class<?> clazz) {
        this.value = parseTableName(clazz);
    }

    private String parseTableName(final Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Table.class)) {
            return clazz.getSimpleName();
        }
        String tableName = clazz.getAnnotation(Table.class).name();
        if (tableName.isEmpty()) {
            return clazz.getSimpleName();
        }
        return tableName;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object entity) {
        if (this == entity) return true;
        if (entity == null || getClass() != entity.getClass()) return false;
        EntityTableName that = (EntityTableName) entity;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
