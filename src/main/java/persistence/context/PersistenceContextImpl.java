package persistence.context;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<Class<?>, Map<String, Object>> FIRST_CACHE = new HashMap<>();

    @Override
    public <T> T getEntity(Class<T> clazz, String id) {
        Map<String, Object> entityMap = FIRST_CACHE.get(clazz);
        if (entityMap == null) {
            return null;
        }
        return clazz.cast(entityMap.get(id));
    }

    @Override
    public <T> void addEntity(T inserted, String instanceId) {
        Class<?> clazz = inserted.getClass();
        Map<String, Object> entityMap = getOrCreateEntityMap(clazz);

        entityMap.put(instanceId, inserted);

        FIRST_CACHE.put(clazz, entityMap);
    }

    @Override
    public <T> void removeEntity(T instance, String instanceId) {
        Class<?> clazz = instance.getClass();
        Map<String, Object> entityMap = FIRST_CACHE.get(clazz);

        entityMap.remove(instanceId);

        if (entityMap.isEmpty()) {
            FIRST_CACHE.remove(clazz);
        }
    }

    private <T> Map<String, Object> getOrCreateEntityMap(Class<T> clazz) {
        return FIRST_CACHE.computeIfAbsent(clazz, k -> new HashMap<>());
    }
}
