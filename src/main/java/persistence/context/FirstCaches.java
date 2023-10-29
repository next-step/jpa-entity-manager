package persistence.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FirstCaches {
    private final Map<Class<?>, Map<String, Object>> firstCaches = new HashMap<>();

    public void putFirstCache(Object instance, String instanceId) {
        if (instance == null) {
            return;
        }
        firstCaches.computeIfAbsent(instance.getClass(), k -> new HashMap<>())
                .put(instanceId, instance);
    }

    public Object getFirstCache(Class<?> clazz, String id) {
        return Optional.ofNullable(firstCaches.get(clazz))
                .map(firstCache -> firstCache.get(id))
                .orElse(null);
    }

    public void remove(Class<?> clazz, String instanceId) {
        Optional.ofNullable(firstCaches.get(clazz))
                .ifPresent(firstCache -> {
                    firstCache.remove(instanceId);
                    if (firstCache.isEmpty()) {
                        firstCaches.remove(clazz);
                    }
                });
    }
}
