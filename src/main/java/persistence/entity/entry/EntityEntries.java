package persistence.entity.entry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class EntityEntries {
    private final Map<Class<?>, Map<String, EntityEntry>> entityEntries = new HashMap<>();

    public void changeOrSetStatus(Status toStatus, Class<?> clazz, String instanceId) {
        Map<String, EntityEntry> entityEntryMap = entityEntries.computeIfAbsent(clazz, k -> new HashMap<>());

        entityEntryMap.putIfAbsent(instanceId, new SimpleEntityEntry(toStatus));
    }

    public EntityEntry getEntityEntry(Class<?> clazz, String instanceId) {
        return Optional.ofNullable(entityEntries.get(clazz))
                .map(it -> it.get(instanceId))
                .orElse(new SimpleEntityEntry(Status.LOADING));
    }
}
