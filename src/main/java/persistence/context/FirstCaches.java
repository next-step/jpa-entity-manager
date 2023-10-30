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

    public Object getFirstCacheOrNull(Class<?> clazz, String id) {
        return Optional.ofNullable(firstCaches.get(clazz))
                .map(firstCache -> firstCache.get(id))
                .orElse(null);
    }

    public void remove(Class<?> clazz, String instanceId) {
        Optional.ofNullable(firstCaches.get(clazz)).ifPresent(firstCacheMap -> {
            Object firstCache = firstCacheMap.get(instanceId);
            if (firstCache == null) {
                throw new IllegalArgumentException(
                        String.format("Class: %s Id: %s 에 해당하는 일차 캐시가 없습니다.", clazz.getSimpleName(), instanceId));
            }
        });
    }
}
