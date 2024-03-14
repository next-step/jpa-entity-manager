package persistence.entity.context.cache;

import java.util.HashMap;
import java.util.Map;

public class EntitySnapshots {

    private final Map<EntityKey, EntitySnapshot> entitySnapshots = new HashMap<>();

    public void add(final EntityKey entityKey, final Object entity) {
        entitySnapshots.put(entityKey, new EntitySnapshot(entity));
    }

    public EntitySnapshot get(final EntityKey entityKey, final Object entity) {
        return entitySnapshots.computeIfAbsent(entityKey, key -> new EntitySnapshot(entity));
    }

    public void remove(final EntityKey entityKey) {
        entitySnapshots.remove(entityKey);
    }

}
