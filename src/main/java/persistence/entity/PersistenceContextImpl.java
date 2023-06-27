package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<Long, Object> entitiesByKey = new HashMap<>();

    @Override
    public Object getEntity(Long id) {
        return entitiesByKey.get(id);
    }

    @Override
    public void addEntity(Long id, Object entity) {
        entitiesByKey.put(id, entity);
    }

    @Override
    public void removeEntity(Long id) {
        entitiesByKey.remove(id);
    }
}
