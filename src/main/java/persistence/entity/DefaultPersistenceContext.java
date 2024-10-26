package persistence.entity;

import jdbc.InstanceFactory;
import persistence.sql.meta.EntityKey;
import persistence.sql.meta.EntityTable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DefaultPersistenceContext implements PersistenceContext {
    private final Map<EntityKey, Object> entityRegistry = new ConcurrentHashMap<>();
    private final Map<EntityKey, Object> entitySnapshotRegistry = new ConcurrentHashMap<>();
    private final Map<Object, EntityEntry> entityEntryRegistry = new ConcurrentHashMap<>();

    private final Queue<Object> persistQueue = new ConcurrentLinkedQueue<>();
    private final Queue<Object> removeQueue = new ConcurrentLinkedQueue<>();

    @Override
    public void addEntity(Object entity) {
        final EntityTable entityTable = new EntityTable(entity);
        addEntity(entity, entityTable);
        addSnapshot(entity, entityTable);
    }

    @Override
    public <T> T getEntity(Class<T> entityType, Object id) {
        final EntityKey entityKey = new EntityKey(entityType, id);
        return entityType.cast(entityRegistry.get(entityKey));
    }

    @Override
    public void removeEntity(Object entity) {
        final EntityTable entityTable = new EntityTable(entity);
        entityRegistry.remove(entityTable.toEntityKey());
        entitySnapshotRegistry.remove(entityTable.toEntityKey());
        createOrUpdateStatus(entity, EntityStatus.GONE);
    }

    @Override
    public <T> T getSnapshot(Class<T> entityType, Object id) {
        final EntityKey entityKey = new EntityKey(entityType, id);
        return entityType.cast(entitySnapshotRegistry.get(entityKey));
    }

    @Override
    public void addToPersistQueue(Object entity) {
        persistQueue.offer(entity);
        createOrUpdateStatus(entity, EntityStatus.MANAGED);
    }

    @Override
    public void addToRemoveQueue(Object entity) {
        removeQueue.offer(entity);
        createOrUpdateStatus(entity, EntityStatus.DELETED);
    }

    @Override
    public Queue<Object> getPersistQueue() {
        return persistQueue;
    }

    @Override
    public Queue<Object> getRemoveQueue() {
        return removeQueue;
    }

    @Override
    public List<Object> getAllEntity() {
        return new ArrayList<>(entityRegistry.values());
    }

    @Override
    public EntityEntry getEntityEntry(Object entity) {
        return entityEntryRegistry.get(entity);
    }

    @Override
    public void createOrUpdateStatus(Object entity, EntityStatus entityStatus) {
        final EntityEntry entityEntry = entityEntryRegistry.get(entity);
        if (Objects.isNull(entityEntry)) {
            final EntityEntry entityEntry1 = new EntityEntry(entityStatus);
            entityEntryRegistry.put(entity, entityEntry1);
            return;
        }
        entityEntry.updateStatus(entityStatus);
    }

    private void addEntity(Object entity, EntityTable entityTable) {
        entityRegistry.put(entityTable.toEntityKey(), entity);
    }

    private void addSnapshot(Object entity, EntityTable entityTable) {
        final Object snapshot = new InstanceFactory<>(entity.getClass()).copy(entity);
        entitySnapshotRegistry.put(entityTable.toEntityKey(), snapshot);
    }
}
