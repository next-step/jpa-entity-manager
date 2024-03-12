package persistence.entity.context.cache;

import java.util.HashMap;
import java.util.Map;

public class EntitySnapshots {

    private final Map<EntityKey<?>, EntitySnapshot> entitySnapshots = new HashMap<>();

    public void add(final EntityKey<?> entityKey, final Object entity) {
        entitySnapshots.put(entityKey, new EntitySnapshot(entity));
    }

    public void remove(final EntityKey<?> entityKey) {
        entitySnapshots.remove(entityKey);
    }

    public boolean compareWithSnapshot(final EntityKey<?> entityKey, final Object entity) {
        return this.entitySnapshots.get(entityKey).isSame(entity);
    }

}
