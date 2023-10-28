package persistence.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<Integer, Object> contextMap;

    PersistenceContextImpl() {
        this.contextMap = new ConcurrentHashMap<>();
    }

    @Override
    public Object getEntity(Integer id) {
        if(contextMap.containsKey(id)) {
            return contextMap.get(id);
        }
        return null;
    }

    @Override
    public void addEntity(Integer id, Object entity) {
        contextMap.put(id, entity);
    }

    @Override
    public void removeEntity(Integer id) {
        contextMap.remove(id);
    }

    @Override
    public boolean isValidEntity(Integer id) {
        return contextMap.containsKey(id);
    }
}
