package persistence.core;

import java.util.HashMap;
import java.util.Map;

public class DefaultPersistenceContext implements PersistenceContext {

    private Map<Long, Object> cachedEntities;

    public DefaultPersistenceContext() {
        this.cachedEntities = new HashMap<>();
    }

    @Override
    public Object getEntity(Long id) {
        return cachedEntities.get(id);
    }

    @Override
    public void addEntity(Long id, Object entity) {
        cachedEntities.put(id, entity);
    }

    @Override
    public void removeEntity(Object entity) {
        cachedEntities.values().remove(entity);
    }
}
