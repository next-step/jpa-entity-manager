package persistence.entity;

import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<EntityKey, Object> managedEntities = Map.of();

    @Override
    public Object findEntity(EntityKey id) {
        return managedEntities.get(id);
    }

    @Override
    public void addEntity(EntityKey id, Object entity) {
        managedEntities.put(id, entity);
    }
}
