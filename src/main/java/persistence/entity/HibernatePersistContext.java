package persistence.entity;

import persistence.entity.event.Event;
import persistence.jpa.Cache;
import persistence.jpa.SnapShot;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class HibernatePersistContext implements PersistenceContext {

    private final Cache cache;
    private final SnapShot snapshot;
    private final EntityEntry entityEntry;
    private final Queue<Event> actionQueue;

    public HibernatePersistContext() {
        this.cache = new Cache();
        this.snapshot = new SnapShot();
        this.entityEntry = new EntityEntry();
        this.actionQueue = new LinkedList<>();
    }

    @Override
    public <T> Optional<T> getEntity(Class<T> clazz, Object id) {
        EntityKey entityKey = new EntityKey(clazz, id);
        entityEntry.updateState(entityKey, EntityStatus.LOADING);
        Optional<Object> entity = cache.get(entityKey);
        if (entity.isPresent()) {
            entityEntry.updateState(entityKey, EntityStatus.MANAGED);
        }
        return (Optional<T>) entity;
    }

    @Override
    public void addEntity(Object entity, Object id) {
        EntityKey entityKey = new EntityKey(entity.getClass(), id);
        entityEntry.updateState(entityKey, EntityStatus.SAVING);
        cache.save(entityKey, entity);
    }

    @Override
    public void removeEntity(Class<?> clazz, Object id) {
        EntityKey entityKey = new EntityKey(clazz, id);
        entityEntry.updateState(entityKey, EntityStatus.DELETED);
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
    public Queue<Event> getActionQueue() {
        return actionQueue;
    }

    @Override
    public void addActionQueue(Event event) {
        actionQueue.add(event);
    }
}
