package persistence.util;

import java.lang.reflect.Field;

public class ReflectionCopy {

    public static Object copy(Object original) {
        try {
            Object copy = original.getClass().getDeclaredConstructor().newInstance();

            for (Field field : original.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                field.set(copy, field.get(original));
            }
            return copy;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
