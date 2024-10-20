package persistence.sql.entity;

import jakarta.persistence.Table;

public class EntityTable {
    private final String tableName;

    public EntityTable(String tableName) {
        this.tableName = tableName;
    }

    public static EntityTable from(Class<?> clazz) {
        return new EntityTable(extractTableName(clazz));
    }

    private static String extractTableName(Class<?> clazz) {
        Table annotation = clazz.getAnnotation(Table.class);
        if (annotation == null) {
            return clazz.getSimpleName().toLowerCase();
        }

        if (!annotation.name().isEmpty()) {
            return annotation.name();
        }

        return clazz.getSimpleName().toLowerCase();
    }

    public String getTableName() {
        return tableName;

    }
}
