package persistence.entity;

import java.util.Optional;

public class SimplePersistenceContext implements PersistenceContext {

    private final EntityCache entities;
    private final EntitySnapshots entitySnapshots;
    private final EntityEntries entityEntries;

    public SimplePersistenceContext() {
        this.entities = new EntityCache();
        this.entitySnapshots = new EntitySnapshots();
        this.entityEntries = new EntityEntries();
    }

    @Override
    public Optional<Object> getEntity(final EntityKey key) {
        return entities.get(key);
    }

    @Override
    public void addEntity(final EntityKey key, final Object entity) {
        entities.add(key, entity);
        updateEntityEntryStatus(entity, Status.MANAGED);
    }

    @Override
    public void removeEntity(final EntityKey key) {
        entities.remove(key);
    }

    @Override
    public boolean hasEntity(final EntityKey key) {
        return entities.containsKey(key);
    }

    @Override
    public Object getDatabaseSnapshot(final EntityKey key, final Object entity) {
        return entitySnapshots.getDatabaseSnapshot(key, entity);
    }

    @Override
    public void addEntityEntry(final Object entity, final Status status) {
        entityEntries.addEntityEntry(entity, status);
    }

    @Override
    public Optional<EntityEntry> getEntityEntry(final Object entity) {
        return entityEntries.getEntityEntry(entity);
    }

    @Override
    public void updateEntityEntryStatus(final Object entity, final Status status) {
        entityEntries.updateEntityEntryStatus(entity, status);
    }
}
