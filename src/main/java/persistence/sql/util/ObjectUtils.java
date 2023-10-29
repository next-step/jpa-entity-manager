package persistence.sql.util;

import java.lang.reflect.Field;

public class ObjectUtils {

    public static Object copy(Object source) {
        try {
            Class<?> clazz = source.getClass();

            Object target = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                field.set(target, field.get(source));
            }
            return target;
        } catch (Exception e) {
            throw new IllegalStateException("엔티티 복제 도중 오류가 발생하였습니다");
        }
    }
}
