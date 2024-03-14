package persistence.core;

import java.util.HashMap;
import java.util.Map;

public class EntityKeyManager {
    private Map<String, EntityKey> keys;

    public EntityKeyManager() {
        this.keys = new HashMap<>();
    }

    private EntityKey createKey(Class<?> clazz, Long id) {
        EntityKey entityKey = new EntityKey(clazz, id);
        return entityKey;
    }

    public EntityKey from(Class<?> clazz, Long id) {
        String keyString = EntityKey.genEntityKey(clazz.getSimpleName(), id);

        return keys.computeIfAbsent(keyString, key -> createKey(clazz, id));
    }

}
