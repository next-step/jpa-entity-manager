package persistence.context;

import persistence.entity.EntityId;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<EntityId, Object> entities = new HashMap<>();

    @Override
    public Object getEntity(EntityId entityId) {
        return entities.get(entityId);
    }

    @Override
    public void addEntity(Long id, Object entity) {

    }

    @Override
    public void removeEntity(Object entity) {

    }
}
