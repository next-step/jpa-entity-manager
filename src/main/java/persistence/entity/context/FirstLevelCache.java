package persistence.entity.context;

import java.util.HashMap;
import java.util.Map;

public class FirstLevelCache {
    private final Map<EntityKey, Object> map;

    public FirstLevelCache() {
        this.map = new HashMap<>();
    }

    public Object find(EntityKey entityKey) {
        return map.get(entityKey);
    }

    public void store(EntityKey entityKey, Object entity) {
        map.put(entityKey, entity);
    }

    public void delete(EntityKey entityKey) {
        map.remove(entityKey);
    }
}
