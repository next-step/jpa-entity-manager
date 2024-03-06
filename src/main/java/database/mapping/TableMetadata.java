package database.mapping;

import jakarta.persistence.Table;

public class TableMetadata {

    private final Class<?> clazz;

    public TableMetadata(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getTableName() {
        Table tableAnnotation = clazz.getAnnotation(Table.class);
        if (tableAnnotation != null && !tableAnnotation.name().isEmpty()) {
            return tableAnnotation.name();
        }
        return clazz.getSimpleName();
    }

    public String getEntityClassName() {
        return clazz.getName();
    }
}
