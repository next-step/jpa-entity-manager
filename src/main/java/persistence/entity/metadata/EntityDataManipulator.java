package persistence.entity.metadata;

import java.lang.reflect.Field;

public class EntityDataManipulator {

    public static Object getValue(Object entity, String columnName) {
        Field field = getFieldByColumnName(entity.getClass(), columnName);

        try {
            return getValue(entity, field);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object getIdValue(Object entity) {
        Field field = getIdField(entity.getClass());
        field.setAccessible(true);
        try {

            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object getValue(Object entity, Field field) throws IllegalAccessException {
        field.setAccessible(true);

        return field.get(entity);
    }

    public static void setValue(Object entity, String columnName, Object value) throws IllegalAccessException {
        Field field = getFieldByColumnName(entity.getClass(), columnName);
        setValue(entity, field, value);
    }

    public static void setIdValue(Object entity, Object value) throws IllegalAccessException {
        Field field = getIdField(entity.getClass());
        setValue(entity, field, value);
    }

    private static void setValue(Object entity, Field field, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(entity, value);
    }

    private static Field getIdField(Class<?> clazz) {
        return DefaultEntityMetadataReader.getIdColumn(clazz).getField();
    }

    private static Field getFieldByColumnName(Class<?> clazz, String columnName) {
        return DefaultEntityMetadataReader.getEntityColumnByColumnName(clazz, columnName).getField();
    }

}
