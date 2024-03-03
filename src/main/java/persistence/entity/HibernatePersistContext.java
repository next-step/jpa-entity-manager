package persistence.entity;

import persistence.entity.event.DeleteEvent;
import persistence.entity.event.UpdateEvent;
import persistence.jpa.Cache;
import persistence.jpa.SnapShot;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

public class HibernatePersistContext implements PersistenceContext {

    private final Cache cache;
    private final SnapShot snapshot;
    private final Map<EntityKey, EntityEntry> entityEntries;
    private final Queue<UpdateEvent> updateActionQueue;
    private final Queue<DeleteEvent> deleteActionQueue;

    public HibernatePersistContext() {
        this.cache = new Cache();
        this.snapshot = new SnapShot();
        this.entityEntries = new HashMap<>();
        this.updateActionQueue = new LinkedList<>();
        this.deleteActionQueue = new LinkedList<>();
    }

    @Override
    public <T> Optional<T> getEntity(Class<T> clazz, Object id) {
        EntityKey entityKey = new EntityKey(clazz, id);
        entityEntries.put(entityKey, new EntityEntry(EntityStatus.LOADING));
        Optional<Object> entity = cache.get(entityKey);
        if (entity.isPresent()) {
            entityEntries.put(entityKey, new EntityEntry(EntityStatus.MANAGED));
        }
        return (Optional<T>) entity;
    }

    @Override
    public void addEntity(Object entity, Object id) {
        EntityKey entityKey = new EntityKey(entity.getClass(), id);
        entityEntries.put(entityKey, new EntityEntry(EntityStatus.SAVING));
        cache.save(entityKey, entity);
    }

    @Override
    public void removeEntity(Class<?> clazz, Object id) {
        EntityKey entityKey = new EntityKey(clazz, id);
        entityEntries.put(entityKey, new EntityEntry(EntityStatus.DELETED));
        cache.remove(entityKey);
    }

    @Override
    public void getDatabaseSnapshot(EntityMetaData entity, Object id) {
        snapshot.save(new EntityKey(entity.getClazz(), id), entity);
    }

    @Override
    public EntityMetaData getCachedDatabaseSnapshot(Class<?> clazz, Object id) {
        return snapshot.get(new EntityKey(clazz, id));
    }

    @Override
    public Queue<DeleteEvent> getDeleteActionQueue() {
        return deleteActionQueue;
    }

    @Override
    public Queue<UpdateEvent> getUpdateActionQueue() {
        return updateActionQueue;
    }

    @Override
    public void addDeleteActionQueue(DeleteEvent event) {
        deleteActionQueue.add(event);
    }

    @Override
    public void addUpdateActionQueue(UpdateEvent event) {
        updateActionQueue.add(event);
    }

    @Override
    public void updateEntityEntryToGone(Object entity, Object id) {
        EntityKey entityKey = new EntityKey(entity.getClass(), id);
        entityEntries.put(entityKey, new EntityEntry(EntityStatus.GONE));
    }

}
