package persistence.fakehibernate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FakePersistenceContext implements PersistenceContext {
    private final Map<Class<?>, Map<Long, Object>> entityCache = new HashMap<>();

    public void add(Object object, Long id) {
        Class<?> clazz = object.getClass();
        entityCache.computeIfAbsent(clazz, k -> new ConcurrentHashMap<>()).put(id, object);
    }

    public Object get(Class<?> clazz, Long id) {
        Map<Long, Object> entityMap = entityCache.get(clazz);
        if (entityMap == null || !entityMap.containsKey(id)) {
            throw new IllegalArgumentException("Entity not found");
        }
        return entityMap.get(id);
    }


    public void update(Object object, Long id) {
        Class<?> clazz = object.getClass();
        entityCache.computeIfAbsent(clazz, k -> new ConcurrentHashMap<>())
                .put(id, object);
    }

    public void remove(Class<?> clazz, Long id) {
        Map<Long, Object> entityMap = entityCache.get(clazz);
        if (entityMap == null || !entityMap.containsKey(id)) {
            throw new IllegalArgumentException("Entity not found");
        }
        entityMap.remove(id);
    }

    public boolean isExist(Class<?> clazz, Long id) {
        Map<Long, Object> entityMap = entityCache.get(clazz);
        return entityMap != null && entityMap.containsKey(id);
    }
}
