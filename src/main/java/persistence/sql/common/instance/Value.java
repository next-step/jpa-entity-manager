package persistence.sql.common.instance;

import jakarta.persistence.Transient;
import utils.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;

public class Value {
    private final Object value;

    private Value(Object object) {
        this.value = object;
    }

    public static <T> Value[] of(T t) {
        Field[] fields = t.getClass().getDeclaredFields();
        return Arrays.stream(fields)
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .map(field -> new Value(extractValue(t, field)))
                .toArray(Value[]::new);
    }

    /**
     * 해당 필드의 값을 추출합니다.
     */
    public static <T> Object extractValue(T t, Field field) {
        try {
            Field fi = t.getClass().getDeclaredField(field.getName());

            fi.setAccessible(true);

            return fi.get(t);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public String getValue() {
        if (value == null) {
            return null;
        }

        return StringUtils.parseChar(value);
    }
}
