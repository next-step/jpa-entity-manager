package persistence.entity.persistencecontext;

import java.util.HashMap;
import java.util.Map;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<EntityKey, Map.Entry<Object, EntitySnapshot>> entities;

    public SimplePersistenceContext() {
        this.entities = new HashMap<>();
    }

    @Override
    public Object getEntity(Class<?> clazz, Object id) {
        if (entities.containsKey(EntityKey.of(clazz, id))) {
            return entities.get(EntityKey.of(clazz, id)).getKey();
        }
        return null;
    }

    @Override
    public void addEntity(Object entity) {
        entities.put(EntityKey.from(entity), Map.entry(entity, EntitySnapshot.from(entity)));
    }

    @Override
    public void removeEntity(Object entity) {
        entities.remove(EntityKey.from(entity));
    }
}
