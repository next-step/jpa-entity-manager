package persistence.core;

import java.util.List;
import java.util.Map;

public class EntityKeyManager {
    private Map<String, EntityKey> keys;

    public EntityKey createKey(Class<?> clazz, Long id) {
        EntityKey entityKey = new EntityKey(clazz, id);
        put(entityKey);
        return entityKey;
    }

    private void put(EntityKey entityKey) {
        keys.put(entityKey.getKey(), entityKey);
    }

    public EntityKey from(Class<?> clazz, Long id) {
        String keyString = EntityKey.genEntityKey(clazz.getSimpleName(), id);

        return keys.computeIfAbsent(keyString, k -> new EntityKey(clazz, id));
    }



}
