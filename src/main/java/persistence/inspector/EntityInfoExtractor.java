package persistence.inspector;

import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;

public class EntityInfoExtractor {

    public static String getColumnName(Field field) {
        return EntityFieldInspector.getColumnName(field);
    }

    public static String getTableName(Class<?> clazz) {
        if (ClsssMetadataInspector.hasAnnotation(clazz, jakarta.persistence.Table.class) && (!clazz.getAnnotation(Table.class).name().isBlank())) {

                return clazz.getAnnotation(Table.class).name();
        }

        return clazz.getSimpleName().toLowerCase();
    }

    public static boolean isPrimaryKey(Field id) {
        return EntityFieldInspector.hasAnnotation(id, Id.class);
    }

    public static List<Field> getColumns(Class<?> clazz) {
        return ClsssMetadataInspector.getAllFields(clazz)
                .stream()
                .filter(EntityFieldInspector::isPersistable)
                    .collect(Collectors.toList());
    }

}
