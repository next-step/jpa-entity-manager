package persistence.entity.impl;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import persistence.entity.EntityEntry;
import persistence.entity.EntityKey;
import persistence.entity.EntitySnapshot;
import persistence.entity.EntityStatus;
import persistence.entity.PersistenceContext;
import persistence.exception.NotExistException;

public class DefaultPersistenceContext implements PersistenceContext {

    private final Map<EntityKey, Object> context = new HashMap<>();
    private final Map<EntityKey, EntitySnapshot> snapshots = new HashMap<>();
    private final Map<EntityKey, EntityEntry> entries = new HashMap<>();

    @Override
    public <T, ID> Optional<T> getEntity(ID id, Class<T> entityType) {
        EntityKey key = new EntityKey(id, entityType);
        return Optional.ofNullable(entityType.cast(context.get(key)));
    }

    @Override
    public void addEntity(Object entity) {
        EntityKey key = new EntityKey(entity);
        if (context.containsKey(key)) {
            context.remove(key);
            context.put(key, entity);
            return;
        }
        context.put(key, entity);
    }

    @Override
    public void removeEntity(Object entity) {
        EntityKey key = new EntityKey(entity);
        context.remove(key);
    }

    @Override
    public void addDatabaseSnapshot(Object entity) {
        EntityKey key = new EntityKey(entity);
        snapshots.put(key, new EntitySnapshot(entity));
    }

    @Override
    public <T> EntitySnapshot getDatabaseSnapshot(T entity) {
        EntityKey key = new EntityKey(entity);
        return snapshots.get(key);
    }

    @Override
    public void addEntityEntry(Object entity, EntityStatus status) {
        EntityKey key = new EntityKey(entity);

        if (entries.containsKey(key)) {
            EntityEntry entry = entries.get(key);
            entry.updateStatus(status);
            return;
        }

        EntityEntry entry = new EntityEntry(key, status);
        entries.put(key, entry);
    }

    @Override
    public void updateEntityEntry(Object entity, EntityStatus status) {
        EntityKey key = new EntityKey(entity);
        EntityEntry entry = entries.get(key);
        if (entry == null) {
            throw new NotExistException(MessageFormat.format("EntityEntry id: {0}, type: {1}", key.key(), entity.getClass().getSimpleName()));
        }
        entry.updateStatus(status);
    }

    @Override
    public <T> boolean isDirty(T entity) {
        EntitySnapshot snapshot = getDatabaseSnapshot(entity);
        return snapshot.hasDifferenceWith(entity);
    }

}
