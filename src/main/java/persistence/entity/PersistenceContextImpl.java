package persistence.entity;

import persistence.entity.entry.EntityEntry;
import persistence.entity.entry.EntityEntryStatus;
import persistence.model.EntityPrimaryKey;

import java.util.*;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<EntityKey, Object> entityCache = new HashMap<>();
    private final Map<EntityKey, EntitySnapshot> entitySnapshots = new HashMap<>();
    private final Map<EntityKey, EntityEntry> entityEntries = new HashMap<>();

    @Override
    public <T> T getEntity(Class<T> entityClass, Object id) {
        EntityKey cacheKey = createEntityKey(entityClass, id);
        return entityClass.cast(entityCache.get(cacheKey));
    }

    @Override
    public void addEntry(Class<?> entityClass, Object id, EntityEntryStatus entryStatus) {
        EntityKey entityKey = createEntityKey(entityClass, id);
        if (entityEntries.containsKey(entityKey)) {
            throw new IllegalArgumentException("ENTITY_ENTRY ALREADY ADDED");
        }
        entityEntries.put(entityKey, new EntityEntry(entryStatus));
    }

    @Override
    public void addEntry(Object entityObject, EntityEntryStatus entryStatus) {
        EntityKey entityKey = createEntityKey(entityObject);
        if (entityEntries.containsKey(entityKey)) {
            throw new IllegalArgumentException("ENTITY_ENTRY ALREADY ADDED");
        }
        if (entityKey.isValid()) { // XXX: entityKey inValid한 경우 로그로 info 처리하면 좋을 듯 하다
            entityEntries.put(entityKey, new EntityEntry(entryStatus));
        }
    }

    @Override
    public void updateEntry(Object entityObject, EntityEntryStatus entryStatus) {
        EntityKey entityKey = createEntityKey(entityObject);
        if (!entityEntries.containsKey(entityKey)) {
            throw new IllegalArgumentException("ENTITY_ENTRY NOT EXISTS");
        }
        entityEntries.get(entityKey).setStatus(entryStatus);
    }

    @Override
    public void addEntity(Object entityObject) {
        EntityKey entityKey = createEntityKey(entityObject);

        entityCache.put(entityKey, entityObject);
        entitySnapshots.put(entityKey, new EntitySnapshot(entityObject));
        if (entityEntries.containsKey(entityKey)) {
            updateEntry(entityObject, EntityEntryStatus.MANAGED);
            return;
        }
        addEntry(entityObject, EntityEntryStatus.MANAGED);
    }

    @Override
    public void removeEntity(Object entityObject) {
        EntityKey entityKey = createEntityKey(entityObject);
        if (!entityKey.isValid()) {
            throw new IllegalArgumentException("INVALID PK TO TRACK CONTEXT");
        }
        if (!entityCache.containsKey(entityKey)) {
            throw new IllegalArgumentException("ENTITY NOT EXISTS");
        }

        EntityEntry entityEntry = entityEntries.get(entityKey);
        if (!entityEntry.isDeletable()) {
            throw new IllegalStateException("UNABLE TO DELETE ENTITY FOR ENTRY STATUS : " + entityEntry.getStatus());
        }
        entityCache.remove(entityKey);
        entitySnapshots.remove(entityKey);
        updateEntry(entityObject, EntityEntryStatus.DELETED);
    }

    @Override
    public void updateEntity(Object entityObject) {
        EntityKey entityKey = createEntityKey(entityObject);
        if (!entityCache.containsKey(entityKey)) {
            throw new IllegalArgumentException("ENTITY NOT EXISTS");
        }

        EntityEntry entityEntry = entityEntries.get(entityKey);
        if (!entityEntry.isUpdatable()) {
            throw new IllegalStateException("UNABLE TO UPDATE ENTITY FOR ENTRY STATUS : " + entityEntry.getStatus());
        }

        entityCache.put(entityKey, entityObject);
        EntitySnapshot snapshot = entitySnapshots.get(entityKey);
        snapshot.update(entityObject);
    }

    @Override
    public EntitySnapshot getSnapshot(Object entityObject) {
        EntityKey entityKey = createEntityKey(entityObject);
        return entitySnapshots.get(entityKey);
    }

    @Override
    public List<EntitySnapshot> getDirtySnapshots() {
        return entitySnapshots.values().stream()
                .filter(EntitySnapshot::isDirty)
                .toList();
    }

    @Override
    public boolean isEntityExists(Object entityObject) {
        Class<?> entityClass = entityObject.getClass();
        Object entityId = EntityPrimaryKey.build(entityObject).keyValue();
        final EntityKey entityKey = createEntityKey(entityClass, entityId);
        return entityCache.containsKey(entityKey);
    }

    private boolean canFind(Object entityObject) {
        return EntityPrimaryKey.isBuildable(entityObject);
    }

    private EntityKey createEntityKey(Class<?> entityClass, Object id) {
        EntityPrimaryKey primaryKey = EntityPrimaryKey.build(entityClass, id);
        return new EntityKey(entityClass, primaryKey);
    }

    private EntityKey createEntityKey(Object entityObject) {
        EntityPrimaryKey primaryKey = EntityPrimaryKey.build(entityObject);
        return new EntityKey(entityObject.getClass(), primaryKey);
    }
}
