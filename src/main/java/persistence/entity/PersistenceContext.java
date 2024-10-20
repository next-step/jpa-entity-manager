package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContext {
    private final Map<EntityKey, Object> managedEntities = new HashMap<>();

    public Object findEntity(EntityKey entityKey) {
        return managedEntities.get(entityKey);
    }

    public void addEntity(EntityKey entityKey, Object entity) {
        if (managedEntities.containsKey(entityKey)) {
            return;
        }
        managedEntities.put(entityKey, entity);
    }

    public void removeEntity(EntityKey entityKey) {
        managedEntities.remove(entityKey);
    }
}
