package persistence.core;

import java.util.HashMap;
import java.util.Map;

public class EntityKeyManager {
    private Map<String, EntityKey> keys;

    public EntityKeyManager() {
        this.keys = new HashMap<>();
    }

    public EntityKey createKey(Class<?> clazz, Long id) {
        EntityKey entityKey = new EntityKey(clazz, id);
        put(entityKey);
        return entityKey;
    }

    private void put(EntityKey entityKey) {
        keys.put(entityKey.getKey(), entityKey);
    }

    public EntityKey from(Class<?> clazz, Long id) {
        System.out.println("keys : " + keys);
        String keyString = EntityKey.genEntityKey(clazz.getSimpleName(), id);

        return keys.get(keyString);
    }

}
