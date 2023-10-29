package persistence.context;

import persistence.entity.entry.EntityWrap;
import persistence.entity.entry.SimpleEntityEntry;
import persistence.entity.entry.Status;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FirstCaches {
    private final Map<Class<?>, Map<String, EntityWrap>> firstCaches = new HashMap<>();

    public void putFirstCache(Object instance, String instanceId) {
        if (instance == null) {
            return;
        }
        firstCaches.computeIfAbsent(instance.getClass(), k -> new HashMap<>())
                .put(instanceId, new EntityWrap(instance, new SimpleEntityEntry(Status.PERSISTENT)));
    }

    public Object getFirstCacheOrNull(Class<?> clazz, String id) {
        return Optional.ofNullable(firstCaches.get(clazz))
                .map(firstCache -> firstCache.get(id).getInstance())
                .orElse(null);
    }

    public void remove(Class<?> clazz, String instanceId) {
        Optional.ofNullable(firstCaches.get(clazz)).ifPresent(firstCache -> {
            EntityWrap wrap = firstCache.get(instanceId);
            if (wrap != null) {
                wrap.getEntry().updateStatus(Status.REMOVED);
            }
        });
    }
}
