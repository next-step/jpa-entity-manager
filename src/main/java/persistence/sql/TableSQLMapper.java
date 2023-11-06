package persistence.sql;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Arrays;

public class TableSQLMapper {
    public static String getColumnName(Field field) {
        String columnName = field.getName();
        if (field.isAnnotationPresent(Column.class)) {
            Column column = field.getAnnotation(Column.class);
            if (!column.name().isEmpty()) {
                columnName = column.name();
            }
        }

        return columnName;
    }

    public static Field[] getTableColumnFields(Class<?> clazz) {
        return Arrays
            .stream(clazz.getDeclaredFields())
            .filter(TableSQLMapper::isAvailableField)
            .toArray(Field[]::new);
    }

    private static boolean isAvailableField(Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }

    public static String getTableName(Class<?> clazz) {
        String tableName = clazz.getSimpleName().toLowerCase();
        if (clazz.isAnnotationPresent(Table.class)) {
            Table annotation = clazz.getAnnotation(Table.class);
            if (!annotation.name().isEmpty()) {
                tableName = annotation.name().toLowerCase();
            }
        }

        return tableName;
    }

    public static Object getColumnValue(Field field, Object object) {
        try {
            field.setAccessible(true);
            Object value = field.get(object);
            if (field.isAnnotationPresent(Column.class)) {
                Column column = field.getAnnotation(Column.class);
                if (!column.nullable() && value == null) {
                    throw new RuntimeException(TableSQLMapper.getColumnName(field) + " is not nullable!!");
                }
            }

            return value;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static String changeColumnValueToString(Object value) {
        if (value == null) {
            return "NULL";
        }
        else if (value instanceof String) {
            return SQLEscaper.escapeNameBySingleQuote(value.toString());
        } else {
            return value.toString();
        }
    }
}
