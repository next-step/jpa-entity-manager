package persistence.entity;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class Proxy {
    private final Class<?> clazz;
    private final Object object;

    public Proxy(Class<?> clazz, Object object) {
        this.clazz = clazz;
        this.object = object;
    }

    public static Proxy copyObject(Object original) throws IllegalAccessException {
        try {
            Class<?> clazz = original.getClass();

            Object copy = clazz.getDeclaredConstructor().newInstance();

            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);

                Object value = field.get(original);
                field.set(copy, value);
            }

            return new Proxy(clazz, copy);
        } catch (Exception e) {
            throw new IllegalAccessException(e.getMessage());
        }
    }

    public static boolean isProxy(Object other) {
        return other.getClass().isAssignableFrom(Proxy.class);
    }

    public Proxy toDirty(Proxy cacheProxy) {
        Object original = cacheProxy.entity();
        Map<String, Object> originalValues = new HashMap<>();
        try {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Object originalValue = field.get(original);
                originalValues.put(field.getName(), originalValue);
            }

            Proxy dirtyProxy = null;
            dirtyProxy = new Proxy(clazz, clazz.getDeclaredConstructor().newInstance());
            for (Field field : fields) {
                field.setAccessible(true);
                Object originalValue = originalValues.get(field.getName());
                Object currentValue = field.get(object);

                if (originalValue == null && currentValue != null ||
                        originalValue != null && !originalValue.equals(currentValue)) {
                    field.set(dirtyProxy.entity(), currentValue);
                }
            }

            return dirtyProxy;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> entityClass() {
        return clazz;
    }

    public Object entity() {
        return object;
    }
}
