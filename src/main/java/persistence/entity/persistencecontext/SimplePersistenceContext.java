package persistence.entity.persistencecontext;

import java.util.HashMap;
import java.util.Map;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<EntityKey, Object> entities;

    public SimplePersistenceContext() {
        this.entities = new HashMap<>();
    }

    @Override
    public Object getEntity(Class<?> clazz, Object id) {
        return entities.get(EntityKey.of(clazz, id));
    }

    @Override
    public void addEntity(Object entity) {
        entities.put(EntityKey.from(entity), entity);
    }

    @Override
    public void removeEntity(Object entity) {
        entities.remove(EntityKey.from(entity));
    }
}
