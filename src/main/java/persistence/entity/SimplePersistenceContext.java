package persistence.entity;

import persistence.exception.PersistenceException;
import persistence.util.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<EntityKey, Object> entities;
    private final Map<EntityKey, Object> entitySnapshots;
    private final Map<EntityKey, EntityEntry> entityEntries;

    public SimplePersistenceContext() {
        this.entities = new HashMap<>();
        this.entitySnapshots = new HashMap<>();
        this.entityEntries = new HashMap<>();
    }

    @Override
    public Optional<Object> getEntity(final EntityKey key) {
        return Optional.ofNullable(entities.get(key));
    }

    @Override
    public void addEntity(final EntityKey key, final Object entity) {
        entities.put(key, entity);
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
        return entitySnapshots.computeIfAbsent(key, entityKey -> ReflectionUtils.shallowCopy(entity));
    }

    @Override
    public void addEntityEntry(final EntityKey key, final Status status) {
        entityEntries.put(key, new EntityEntry(key, status));
    }

    @Override
    public Optional<EntityEntry> getEntityEntry(final EntityKey key) {
        return Optional.ofNullable(entityEntries.get(key));
    }

    @Override
    public void updateEntityEntryStatus(final EntityKey entityKey, final Status status) {
        final EntityEntry entityEntry = getEntityEntry(entityKey)
                .orElseThrow(() -> new PersistenceException("PersistenceContext 내에 관리되고 있지 않은 Entity 입니다."));
        entityEntry.updateStatus(status);
    }
}
