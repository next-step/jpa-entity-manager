package persistence.fakehibernate;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FakePersistenceContext implements PersistenceContext {
    private final Map<Class<?>, Map<Long, Object>> entityCache = new HashMap<>();

    public Object add(Object object, Long id) {
        Map<Long, Object> longObjectMap = entityCache.get(object.getClass());
        Object o = longObjectMap.get(id);
        ;

        if (Objects.isNull(o)) {
            longObjectMap.put(id, o);
        }
        return o;
    }

    public Object get(Object object, Long id) {
        Map<Long, Object> longObjectMap = entityCache.get(object.getClass());
        return longObjectMap.get(id);
    }

    public void update(Object object, Long id) {
        if (!isExist(object, id)) {
            add(object, id);
        }
        Map<Long, Object> longObjectMap = entityCache.get(object.getClass());
        longObjectMap.put(id, object);
    }

    public void remove(Object object, Long id) {
        Map<Long, Object> longObjectMap = entityCache.get(object.getClass());
    }

    public boolean isExist(Object object, Long id) {
        Map<Long, Object> longObjectMap = entityCache.get(object.getClass());
        return longObjectMap.containsKey(id);
    }
}
