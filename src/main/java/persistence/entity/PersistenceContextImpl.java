package persistence.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<Class<?>, Map<Long, EntityEntry>> entryStorage;

    public PersistenceContextImpl() {
        this.entryStorage = new HashMap<>();
    }

    private Map<Long, EntityEntry> getOrCreateEntryMap(Class<?> clazz) {
        if (!entryStorage.containsKey(clazz)) {
            entryStorage.put(clazz, new HashMap<>());
        }
        return entryStorage.get(clazz);
    }

    @Override
    public Object getEntity(Class<?> clazz, Long id) {
        Map<Long, EntityEntry> entityMap = getOrCreateEntryMap(clazz);
        EntityEntry entityEntry = entityMap.get(id);
        if (entityEntry == null) {
            return null;
        }
        return entityEntry.getEntity();
    }

    @Override
    public void putEntity(Object entity) {
        Class<?> clazz = entity.getClass();
        Long idValue = EntityUtils.getIdValue(entity);

        Map<Long, EntityEntry> entryMap = getOrCreateEntryMap(clazz);

        EntityEntry entityEntry = EntityEntry.of(EntityStatus.MANAGED, entity);
        entryMap.put(idValue, entityEntry);
    }

    @Override
    public void removeEntity(Object entity) {
        Class<?> clazz = entity.getClass();
        Map<Long, EntityEntry> entryMap = getOrCreateEntryMap(clazz);
        Long idValue = EntityUtils.getIdValue(entity);
        EntityEntry entityEntry = entryMap.get(idValue);
        entityEntry.updateStatus(EntityStatus.DELETED);
    }

    @Override
    public Map<Class<?>, List<EntityEntry>> getEntityEntries() {
        return entryStorage.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> new ArrayList<>(entry.getValue().values())
                ));
    }
}
