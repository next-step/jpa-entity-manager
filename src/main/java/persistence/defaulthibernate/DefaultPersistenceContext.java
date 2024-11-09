package persistence.defaulthibernate;

import persistence.entity.EntityData;
import persistence.entity.EntityKey;

import java.util.*;


public class DefaultPersistenceContext implements PersistenceContext {
    private final Map<EntityKey, EntityData> entitiesByKey = new HashMap<>();
    private final Map<EntityKey, EntityEntry> entityEntries = new HashMap<>();

    @Override
    public void add(EntityData entityData, EntityKey entityKey) {
        entitiesByKey.put(entityKey, entityData);
        EntityEntry entry = new EntityEntry(
                EntryStatus.MANAGED,
                entityData.entity(),
                (Long)entityData.getId(),
                createSnapshot(entityData)
        );
        entityEntries.put(entityKey, entry);
    }

    @Override
    public EntityData get(EntityKey entityKey) {
        if (!entitiesByKey.containsKey(entityKey)) {
            throw new IllegalArgumentException("Entity not found");
        }

        if (!entityEntries.containsKey(entityKey)) {
            EntityData entityData = entitiesByKey.get(entityKey);
            entityEntries.put(entityKey, new EntityEntry(
                    EntryStatus.MANAGED,
                    entityData.entity(),
                    (Long)entityData.getId(),
                    createSnapshot(entityData)
            ));
        }

        return entitiesByKey.get(entityKey);
    }

    @Override
    public void update(EntityData entityData, EntityKey entityKey) {
        entitiesByKey.put(entityKey, entityData);

        EntityEntry entry = entityEntries.get(entityKey);
        if (entry != null) {
            entry.setStatus(EntryStatus.MANAGED);
        } else {
            entityEntries.put(entityKey, new EntityEntry(
                    EntryStatus.MANAGED,
                    entityData.entity(),
                    (Long)entityData.getId(),
                    createSnapshot(entityData)
            ));
        }
    }

    @Override
    public void remove(EntityKey entityKey) {
        entitiesByKey.remove(entityKey);
        entityEntries.remove(entityKey);
    }

    public List<Object> getDirtyObjects() {
        return entityEntries.entrySet().stream()
                .filter(entry -> {
                    EntityKey key = entry.getKey();
                    EntityData currentEntityData = entitiesByKey.get(key);
                    return isDirty(currentEntityData, entry.getValue());
                })
                .map(entry -> entitiesByKey.get(entry.getKey()).entity())
                .toList();
    }

    private boolean isDirty(EntityData currentEntityData, EntityEntry entry) {
        if (currentEntityData == null) return false;
        Object[] currentSnapshot = createSnapshot(currentEntityData);
        return !Arrays.equals(currentSnapshot, entry.getLoadedState());
    }

    private Object[] createSnapshot(EntityData entityData) {
        // 엔티티의 모든 필드값을 배열로 변환
        return Arrays.stream(entityData.entityClass().getDeclaredFields())
                .map(field -> {
                    try {
                        field.setAccessible(true);
                        return field.get(entityData.entity());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Failed to create snapshot", e);
                    }
                })
                .toArray();
    }

    public void setEntityEntryStatus(EntityKey entityKey, EntryStatus status) {
        EntityEntry entry = entityEntries.get(entityKey);
        if (entry != null) {
            entry.setStatus(status);
        } else if (status != EntryStatus.GONE) {
            EntityData entityData = entitiesByKey.get(entityKey);
            if (entityData != null) {
                entityEntries.put(entityKey, new EntityEntry(
                        status,
                        entityData.entity(),
                        (Long)entityData.getId(),
                        createSnapshot(entityData)
                ));
            }
        }
    }

    public void clearSnapshots() {
        entityEntries.clear();
    }

    public boolean isExist(EntityKey entityKey) {
        return entitiesByKey.containsKey(entityKey);
    }
}
