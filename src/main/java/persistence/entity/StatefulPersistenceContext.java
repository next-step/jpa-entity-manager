package persistence.entity;

import java.util.HashMap;

public class StatefulPersistenceContext implements PersistenceContext {

    private final HashMap<EntityKey, Object> entitiesByKey;

    public StatefulPersistenceContext(HashMap<EntityKey, Object> entitiesByKey) {
        this.entitiesByKey = entitiesByKey;
    }

    public StatefulPersistenceContext() {
        this(new HashMap<>());
    }


    @Override
    public Object removeEntity(EntityKey key) {
        return entitiesByKey.remove(key);
    }

    @Override
    public Object getEntity(EntityKey key) {
        return entitiesByKey.get(key);
    }

    @Override
    public boolean containsEntity(EntityKey key) {
        return entitiesByKey.containsKey(key);
    }

    @Override
    public void addEntity(EntityKey key, Object entity) {
        entitiesByKey.put(key, entity);
    }
}
