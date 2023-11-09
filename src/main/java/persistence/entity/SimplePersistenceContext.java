package persistence.entity;

import java.util.HashMap;
import java.util.Map;

public class SimplePersistenceContext implements PersistenceContext {

    private static final Map<EntityKey, Object> entityMap = new HashMap<>();
    private static final Map<EntityKey, Object> entitySnapshotMap = new HashMap<>();
    private static final Map<Object, EntityEntry> entityEntryContext = new HashMap<>();

    @Override
    public Object getEntity(EntityKey entityKey) {
        return entityMap.get(entityKey);
    }

    @Override
    public void addEntity(EntityKey entityKey, Object entity) {
        entityMap.put(entityKey, entity);

    }

    @Override
    public void removeEntity(EntityKey entityKey) {
        entityMap.remove(entityKey);
    }

    @Override
    public Object getDatabaseSnapshot(EntityKey entityKey, Object entity) {
        Object snapshot = entitySnapshotMap.get(entityKey);
        if (snapshot != null) {
            return snapshot;
        }
        return entitySnapshotMap.put(entityKey, entity);
    }

    public void addEntityEntry(Object object, EntityEntry entityEntry) {
        entityEntryContext.put(object, entityEntry);
    }

    @Override
    public EntityEntry getEntityEntry(Object object) {
        return entityEntryContext.get(object);
    }

    public void deleteEntityEntry(Object object) {
        entityEntryContext.remove(object);
    }

    public void updateEntityEntryStatus(Object object, Status status) {
        EntityEntry entityEntry = entityEntryContext.get(object);
        if (entityEntry == null) {
            throw new IllegalArgumentException("주어진 object에 해당하는 entity가 entityContext에 없습니다.");
        }
        entityEntry.updateStatus(status);
    }

}
