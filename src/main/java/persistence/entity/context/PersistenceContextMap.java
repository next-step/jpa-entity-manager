package persistence.entity.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenceContextMap {
    private final Map<Class<?>, PersistenceContext<?>> persistenceContextMap = new ConcurrentHashMap<>();

    public PersistenceContext<?> get(Class<?> cls) {
        return persistenceContextMap.get(cls);
    }

    public boolean containsKey(Class<?> cls) {
        return this.persistenceContextMap.containsKey(cls);
    }

    public void put(Class<?> cls, PersistenceContext<?> persistenceContext) {
        persistenceContextMap.put(cls, persistenceContext);
    }

    public void clear() {
        persistenceContextMap.forEach((key, value) -> {
            value.clear();
        });
        persistenceContextMap.clear();
    }
}
