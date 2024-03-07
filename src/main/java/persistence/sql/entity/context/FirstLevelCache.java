package persistence.sql.entity.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FirstLevelCache {

    private final Map<EntityKey, Object> entities;

    public FirstLevelCache() {
        this.entities = new ConcurrentHashMap<>();
    }

    public Object get(final EntityKey entityKey) {
        return entities.get(entityKey);
    }

    public void put(final EntityKey entityKey, final Object value) {
        entities.put(entityKey, value);
    }

    public void remove(final EntityKey entityKey) {
        entities.remove(entityKey);
    }

    public void clear() {
        entities.clear();
    }
}
