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
        EntityKey key = EntityKey.of(clazz, id);
        if (entities.containsKey(key)) {
            return entities.get(key).getKey();
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

    @Override
    public EntitySnapshot getCachedDatabaseSnapshot(Object entity) {
        return entities.get(EntityKey.from(entity)).getValue();
    }
}
