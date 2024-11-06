package persistence.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PersistedEntities {

    private final Map<EntityKey, Object> persistedEntities = new HashMap<>();

    public Object findEntity(EntityKey entityKey) {
        return persistedEntities.getOrDefault(entityKey, null);
    }

    public void persistEntity(EntityKey entityKey, Object entity) {
        persistedEntities.put(entityKey, entity);
    }

    public void removeEntity(EntityKey entityKey) {
        persistedEntities.remove(entityKey);
    }

    public Collection<Object> getEntities() {
        return persistedEntities.values();
    }

    public void clear() {
        persistedEntities.clear();
    }

}
