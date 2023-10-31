package hibernate.entity.persistencecontext;

import hibernate.EntityEntry;
import hibernate.Status;
import hibernate.entity.EntityEntryContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<EntityKey, Object> entities;
    private final Map<EntityKey, EntitySnapshot> snapshotEntities;
    private final EntityEntryContext entityEntryContext;

    public SimplePersistenceContext(
            final Map<EntityKey, Object> entities,
            final Map<EntityKey, EntitySnapshot> snapshotEntities,
            final EntityEntryContext entityEntryContext
    ) {
        this.entities = entities;
        this.snapshotEntities = snapshotEntities;
        this.entityEntryContext = entityEntryContext;
    }

    public SimplePersistenceContext() {
        this(new ConcurrentHashMap<>(), new ConcurrentHashMap<>(), new EntityEntryContext());
    }

    @Override
    public Object getEntity(final EntityKey id) {
        return entities.get(id);
    }

    @Override
    public void addEntity(final Object id, final Object entity) {
        addEntity(id, entity, Status.MANAGED);
    }

    @Override
    public EntityEntry addEntity(Object id, Object entity, Status status) {
        entities.put(new EntityKey(id, entity.getClass()), entity);
        EntitySnapshot entitySnapshot = new EntitySnapshot(entity);
        snapshotEntities.put(new EntityKey(id, entity.getClass()), entitySnapshot);
        EntityEntry entityEntry = new EntityEntry(status);
        entityEntryContext.addEntityEntry(entity, entityEntry);
        return entityEntry;
    }

    @Override
    public void addEntityEntry(Object entity, Status status) {
        entityEntryContext.addEntityEntry(entity, new EntityEntry(status));
    }

    @Override
    public void removeEntity(final Object entity) {
        EntityKey entityKey = entities.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(entity))
                .findAny()
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new IllegalStateException("영속화되어있지 않은 entity입니다."));
        EntityEntry entityEntry = entityEntryContext.getEntityEntry(entity);
        entityEntry.updateStatus(Status.DELETED);
        entities.remove(entityKey);
        snapshotEntities.remove(entityKey);
        entityEntry.updateStatus(Status.GONE);
    }

    @Override
    public EntitySnapshot getDatabaseSnapshot(final EntityKey id) {
        return snapshotEntities.get(id);
    }
}
