package persistence.model.util;

import jakarta.persistence.Column;
import jakarta.persistence.Table;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

public class ReflectionUtil {
    public static <T extends Annotation> Optional<T> getAnnotationIfPresent(Field field, Class<T> annotationClass) {
        if (field.isAnnotationPresent(annotationClass)) {
            return Optional.ofNullable(field.getAnnotation(annotationClass));
        }
        return Optional.empty();
    }

    public static <T extends Annotation> Optional<T> getAnnotationIfPresent(Class<?> clazz, Class<T> annotationClass) {
        if (clazz.isAnnotationPresent(annotationClass)) {
            return Optional.ofNullable(clazz.getAnnotation(annotationClass));
        }
        return Optional.empty();
    }

    public static String getColumnName(Field field) {
        return getAnnotationIfPresent(field, Column.class)
                .map(Column::name)
                .filter(name -> !name.isEmpty())
                .orElse(field.getName());
    }

    public static <T extends Annotation> String getFieldName(Class<?> clazz, Class<T> annotationClass) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(annotationClass))
                .map(Field::getName)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("FAILED TO FIND ANNOTATION : " + annotationClass.getName()));
    }

    public static String getTableName(Class<?> clazz) {
        return ReflectionUtil.getAnnotationIfPresent(clazz, Table.class)
                .map(Table::name)
                .orElse(clazz.getSimpleName());
    }

    public static <T extends Annotation> Map.Entry<String, Object> getFieldNameAndValue(Object entityObject, Class<T> annotationClass) {
        Class<?> entityClass = entityObject.getClass();

        Field pkField = Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> getAnnotationIfPresent(field, annotationClass).isPresent())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No primary key found in the entity"));

        String fieldName = getColumnName(pkField);
        Object fieldValue = getColumnValue(entityObject, pkField);

        return new AbstractMap.SimpleEntry<>(fieldName, fieldValue);
    }

    private static Object getColumnValue(Object entityObject, Field field) {
        try {
            field.setAccessible(true);
            return field.get(entityObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access field value", e);
        }
    }
}
