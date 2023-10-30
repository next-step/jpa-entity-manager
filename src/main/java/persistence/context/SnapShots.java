package persistence.context;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SnapShots {
    private final Map<Class<?>, Map<String, Object>> snapShots = new HashMap<>();

    public Object getSnapShotOrNull(Class<?> clazz, String instanceId) {
        return Optional.ofNullable(snapShots.get(clazz))
                .map(snapShot -> snapShot.get(instanceId))
                .orElse(null);
    }

    public void remove(Class<?> clazz, String instanceId) {
        Optional.ofNullable(snapShots.get(clazz))
                .ifPresent(snapShot -> {
                    snapShot.remove(instanceId);
                    if (snapShot.isEmpty()) {
                        snapShots.remove(clazz);
                    }
                });
    }

    public Object putSnapShot(Object instance, String instanceId) {
        if (instance == null) {
            return null;
        }

        Object newSnapshot = createDeepCopy(instance);

        snapShots.computeIfAbsent(instance.getClass(), k -> new HashMap<>())
                .put(instanceId, newSnapshot);

        return newSnapshot;
    }


    private <T> T createDeepCopy(T original) {
        try {
            Class<?> clazz = original.getClass();
            T copy = (T) clazz.getDeclaredConstructor().newInstance();
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(original);
                if (value != null && !isPrimitiveOrWrapper(value.getClass())) {
                    field.set(copy, createDeepCopy(value));
                } else {
                    field.set(copy, value);
                }
            }
            return copy;
        } catch (Exception e) {
            throw new RuntimeException("딥카피 실패", e);
        }
    }

    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || (clazz == Double.class) || (clazz == Float.class) || (clazz == Long.class)
                || (clazz == Integer.class) || (clazz == Short.class) || (clazz == Character.class)
                || (clazz == Byte.class) || (clazz == Boolean.class) || (clazz == String.class);
    }
}
