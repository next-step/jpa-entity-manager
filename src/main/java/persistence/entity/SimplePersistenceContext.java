package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class SimplePersistenceContext implements PersistenceContext {

    private static final Map<EntityKey, Object> entityMap = new HashMap<>();

    @Override
    public Object getEntity(EntityKey entityKey) {
        return entityMap.get(entityKey);
    }

    @Override
    public void addEntity(EntityKey entityKey, Object entity) {
        entityMap.put(entityKey, entity);

    }

    @Override
    public void removeEntity(EntityKey entityKey) {
        entityMap.remove(entityKey);
    }

}
