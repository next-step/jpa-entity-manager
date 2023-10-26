package hibernate.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<EntityKey, Object> entities;

    public SimplePersistenceContext(final Map<EntityKey, Object> entities) {
        this.entities = entities;
    }

    public SimplePersistenceContext() {
        this(new ConcurrentHashMap<>());
    }

    @Override
    public Object getEntity(final Object id) {
        return entities.get(id);
    }

    @Override
    public void addEntity(final Object id, final Object entity) {
        entities.put(new EntityKey(id, entity.getClass()), entity);
    }
}
