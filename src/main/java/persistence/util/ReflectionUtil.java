package persistence.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

public class ReflectionUtil {
    public static void setFieldValue(Object entity, String fieldName, Object value) {
        try {
            Field field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(entity, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("FAILED TO SET FIELD VALUE! : ", e);
        }
    }

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

    public static <T extends Annotation> String getFieldName(Class<?> clazz, Class<T> annotationClass) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> getAnnotationIfPresent(field, annotationClass).isPresent())
                .map(Field::getName)
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("FAILED TO FIND ANNOTATION : " + annotationClass.getName())
                );
    }

    public static <T extends Annotation> String getFieldName(Field field, Class<T> annotationClass) {
        return getAnnotationIfPresent(field, annotationClass)
                .flatMap(annotation -> getAnnotationNameOrEmpty(annotation, annotationClass))
                .orElse(field.getName());
    }

    public static <T extends Annotation> String getClassName(Class<?> clazz, Class<T> annotationClass) {
        return getAnnotationIfPresent(clazz, annotationClass)
                .flatMap(annotation -> getAnnotationNameOrEmpty(annotation, annotationClass))
                .orElse(clazz.getSimpleName());
    }

    public static <T extends Annotation> Map.Entry<String, Object> getFieldNameAndValue(
            Object entityObject,
            Class<T> annotationClass
    ) {
        Class<?> entityClass = entityObject.getClass();

        Field targetField = Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> getAnnotationIfPresent(field, annotationClass).isPresent())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("NO FIELD FOUND WITH " + annotationClass));

        String fieldName = getFieldName(entityObject.getClass(), annotationClass);
        Object fieldValue = getFieldValue(entityObject, targetField);

        return new AbstractMap.SimpleEntry<>(fieldName, fieldValue);
    }

    public static Map<String, Object> getAllFieldNameAndValue(Object entityObject) {
        Map<String, Object> keyValues = new HashMap<>();

        Class<?> entityClass = entityObject.getClass();
        Field[] fields = entityClass.getDeclaredFields();

        Arrays.stream(fields).forEach(field -> {
            field.setAccessible(true);
            try {
                String fieldName = field.getName();
                Object fieldValue = field.get(entityObject);
                keyValues.put(fieldName, fieldValue);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("FAILED TO ACCESS FIELD" + field.getName(), e);
            }
        });

        return keyValues;
    }

    public static Object getFieldValue(Object entity, String fieldName) {
        try {
            Field field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(entity);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to access field value", e);
        }
    }

    private static <T extends Annotation> Optional<String> getAnnotationNameOrEmpty(
            T annotation,
            Class<T> annotationClass
    ) {
        try {
            Object name = annotationClass.getMethod("name").invoke(annotation);
            if (name instanceof String nameStr && !nameStr.isEmpty()) {
                return Optional.of(nameStr);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("FAILED TO GET `name` property. error = " + e);
        }
        return Optional.empty();
    }

    private static Object getFieldValue(Object entityObject, Field field) {
        try {
            field.setAccessible(true);
            return field.get(entityObject);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access field value", e);
        }
    }
}
