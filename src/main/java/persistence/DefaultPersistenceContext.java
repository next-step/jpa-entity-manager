package persistence;

import java.util.HashMap;

public class DefaultPersistenceContext implements PersistenceContext {

    private HashMap<Long, Object> entitiesByKey;

    @Override
    public Object getEntity(Long id) {
        return entitiesByKey == null ? null : entitiesByKey.get(id);
    }

    @Override
    public void addEntity(Long id, Object entity) {
        if (entitiesByKey == null) {
            entitiesByKey = new HashMap<>();
        }

        entitiesByKey.put(id, entity);
    }

    @Override
    public void removeEntity(Object entity) {

    }
}
