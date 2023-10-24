package hibernate.entity;

import jakarta.persistence.Table;

import java.util.Objects;

public class EntityTableName {

    private final String tableName;

    public EntityTableName(final Class<?> clazz) {
        this.tableName = parseTableName(clazz);
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

    public String getTableName() {
        return tableName;
    }

    @Override
    public boolean equals(Object entity) {
        if (this == entity) return true;
        if (entity == null || getClass() != entity.getClass()) return false;
        EntityTableName that = (EntityTableName) entity;
        return Objects.equals(tableName, that.tableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableName);
    }
}
