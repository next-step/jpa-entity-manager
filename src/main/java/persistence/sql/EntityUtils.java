package persistence.sql;

import jakarta.persistence.Column;
import jakarta.persistence.Table;

import java.lang.reflect.Field;

public class EntityUtils {
    static String getName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Table.class)) {
            final Table table = clazz.getAnnotation(Table.class);
            if (table.name().isBlank()) {
                return clazz.getSimpleName().toLowerCase();
            }
            return table.name();
        }
        return clazz.getSimpleName().toLowerCase();
    }

    static String getColumnName(Field field) {
        if (!field.isAnnotationPresent(Column.class)) {
            return field.getName().toLowerCase();
        }
        final Column column = field.getAnnotation(Column.class);
        if (column.name().isBlank()) {
            return field.getName().toLowerCase();
        }
        return column.name();
    }
}
