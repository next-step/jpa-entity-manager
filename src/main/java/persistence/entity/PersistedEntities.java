package persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersistedEntities {

    private final Map<EntityKey, EntityEntry> entityEntries = new HashMap<>();

    public Object findEntity(EntityKey entityKey) {
        if (entityEntries.containsKey(entityKey)) {
            return entityEntries.get(entityKey).getEntity();
        }
        return null;
    }

    public void persistEntity(EntityKey entityKey, Object entity) {
        var entityEntry = new EntityEntry(entity, Status.MANAGED);
        entityEntry.updateSnapshot(entity);
        entityEntries.put(entityKey, entityEntry);
    }

    public void evict(Object entity) {
        var entityKey = getKey(entity);
        entityEntries.remove(entityKey);
    }

    public void changeToDeleteState(EntityKey entityKey) {
        var entityEntry = entityEntries.get(entityKey);
        if (entityEntry == null) {
            return;
        }
        entityEntry.updateStatus(Status.DELETED);
    }

    public List<Object> getDeletedEntities() {
        return entityEntries.values().stream().filter(e -> e.getStatus() == Status.DELETED).map(EntityEntry::getEntity)
            .toList();
    }

    public List<Object> getManagedEntities() {
        return entityEntries.values().stream().filter(e -> e.getStatus() == Status.MANAGED).map(EntityEntry::getEntity)
            .toList();
    }

    public List<String> findDirtyColumns(Object entity) throws IllegalAccessException {
        var entityKey = getKey(entity);
        var entityEntry = entityEntries.get(entityKey);
        return entityEntry.findDirtyColumns(entity);
    }

    public void updateDatabaseSnapshot(Object entity) {
        var entityKey = getKey(entity);
        var entityEntry = entityEntries.get(entityKey);
        entityEntry.updateSnapshot(entity);
    }

    private EntityKey getKey(Object entity) {
        return new EntityKey(new LongTypeId(entity).getId(), entity.getClass().getName());
    }

}
