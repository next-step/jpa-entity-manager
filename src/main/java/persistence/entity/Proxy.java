package persistence.entity;

import java.lang.reflect.Field;

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

    public Class<?> entityClass() {
        return clazz;
    }

    public Object entity() {
        return object;
    }
}
