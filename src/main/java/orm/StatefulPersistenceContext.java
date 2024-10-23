package orm;

import java.util.HashMap;
import java.util.Map;

public class StatefulPersistenceContext implements PersistenceContext {

    private Map<EntityKey, Object> cachedEntities;

    public StatefulPersistenceContext() {
        cachedEntities = new HashMap<>();
    }

    @Override
    public Object getEntity(EntityKey entityKey) {
        return cachedEntities.get(entityKey);
    }

    @Override
    public void addEntity(EntityKey entityKey, Object object) {
        cachedEntities.put(entityKey, object);
    }

    @Override
    public boolean containsEntity(EntityKey key) {
        return cachedEntities.containsKey(key);
    }

    @Override
    public void removeEntity(EntityKey entityKey) {
        cachedEntities.remove(entityKey);
    }

    @Override
    public void clear() {
        cachedEntities = new HashMap<>();
    }
}
