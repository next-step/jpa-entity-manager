package persistence.context;

import java.util.HashMap;
import java.util.Map;

public class BasicPersistentContext implements PersistenceContext {
    private final Map<Long, Object> entityByKey = new HashMap<>();

    @Override
    public Object getEntity(Long id) {
        return entityByKey.get(id);
    }

    @Override
    public void addEntity(Long id, Object entity) {
        entityByKey.put(id, entity);
    }

    @Override
    public void removeEntity(Long id) {
        entityByKey.remove(id);
    }
}
