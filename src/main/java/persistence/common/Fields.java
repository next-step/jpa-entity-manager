package persistence.common;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Fields {
    private final List<Field> fields;

    public Fields(List<Field> fields) {
        this.fields = fields;
    }

    public static Fields of(Class<?> clazz) {
        Field[] declaredFields = clazz.getDeclaredFields();
        return Arrays.stream(declaredFields)
                .collect(Collectors.collectingAndThen(Collectors.toList(), Fields::new));
    }

    public Field getField(Class<? extends Annotation> isAnnotationClass) {
        return fields.stream()
                .filter(field -> field.isAnnotationPresent(isAnnotationClass))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        isAnnotationClass.getName() + " 어노테이션이 선언된 필드가 존재하지 않습니다.")
                );
    }

    public AccessibleField getAccessibleField(Class<? extends Annotation> isAnnotationClass) {
        return new AccessibleField(getField(isAnnotationClass));
    }

    @SafeVarargs
    public final List<Field> getFields(Class<? extends Annotation>... exceptAnnotationClasses) {
        return fields.stream()
                .filter(field -> !hasAnnotationPresent(field, exceptAnnotationClasses))
                .collect(Collectors.toList());
    }

    private static boolean hasAnnotationPresent(
            Field field,
            Class<? extends Annotation>[] annotationClasses
    ) {
        return Arrays.stream(annotationClasses)
                .anyMatch(field::isAnnotationPresent);
    }
}
