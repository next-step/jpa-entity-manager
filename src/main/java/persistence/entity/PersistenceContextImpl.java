package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<EntityKey, Object> managedEntities = new HashMap<>();

    @Override
    public Object findEntity(EntityKey id) {
        return managedEntities.get(id);
    }

    @Override
    public void addEntity(EntityKey id, Object entity) {
        if (managedEntities.containsKey(id)) {
            return;
        }
        managedEntities.put(id, entity);
    }

    @Override
    public void removeEntity(EntityKey id) {
        managedEntities.remove(id);
    }
}
