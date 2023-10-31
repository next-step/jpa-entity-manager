package persistence.entity;

import persistence.util.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;

public class EntitySnapshots {
    private final Map<EntityKey, Object> entitySnapshots;

    public EntitySnapshots() {
        entitySnapshots = new HashMap<>();
    }

    public Object getDatabaseSnapshot(final EntityKey key, final Object entity) {
        return entitySnapshots.computeIfAbsent(key, entityKey -> ReflectionUtils.shallowCopy(entity));
    }
}
