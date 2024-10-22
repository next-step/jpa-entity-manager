package persistence;

import java.lang.reflect.Field;

public class TestUtils {
    private TestUtils() {}

    @SuppressWarnings("unchecked")
    public static <T> T getValueByFieldName(Object obj, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);

        return (T) field.get(obj);
    }
}
