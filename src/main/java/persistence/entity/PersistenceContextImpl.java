package persistence.entity;

import persistence.exception.DuplicateContextException;
import persistence.exception.InvalidContextException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<Integer, Object> contextMap;

    PersistenceContextImpl() {
        this.contextMap = new ConcurrentHashMap<>();
    }

    @Override
    public Object getEntity(Integer id) {
        if(isValidEntity(id)) {
            return contextMap.get(id);
        }
        return null;
    }

    @Override
    public void addEntity(Integer id, Object entity) {
        if(isValidEntity(id)) {
            throw new DuplicateContextException();
        }
        contextMap.put(id, entity);
    }

    @Override
    public void removeEntity(Integer id) {
        if(!isValidEntity(id)) {
            throw new InvalidContextException();
        }
        contextMap.remove(id);
    }

    @Override
    public boolean isValidEntity(Integer id) {
        return contextMap.containsKey(id);
    }
}
