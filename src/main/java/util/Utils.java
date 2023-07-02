package util;

import java.lang.reflect.Field;

public class Utils {

    public static <T> T copyObject(T source) {
        Class<?> clazz = source.getClass();
        T copy;
        try {
            copy = (T) clazz.newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object value = field.get(source);
                field.set(copy, value);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return copy;
    }
}
