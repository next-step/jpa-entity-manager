package persistence.context;

import persistence.entity.EntityEntry;
import persistence.entity.EntityKey;
import persistence.sql.metadata.EntityMetadata;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SimplePersistenceContext implements PersistenceContext {

    private final Map<EntityKey, Object> firstLevelCache = new HashMap<>();
    private final Map<EntityKey, EntitySnapshot> snapshots = new HashMap<>();
    private final Map<EntityKey, EntityEntry> entries = new HashMap<>();

    @Override
    public <T> T getEntity(Class<T> clazz, Object id) {
        EntityKey key = EntityKey.of(clazz, id);
        EntityEntry entry = entries.get(key);

        if (Objects.isNull(entry)) {
            return null;
        }

        if (entry.isGone()) {
            throw new IllegalStateException("Entity has been removed");
        }


        return clazz.cast(firstLevelCache.get(key));
    }

    @Override
    public void addEntity(Object id, Object entity) {
        if (Objects.isNull(entity)) {
            return;
        }

        EntityKey key = EntityKey.of(entity.getClass(), id);
        EntityEntry entry = entries.get(key);


        if (Objects.isNull(entry)) {
            entry = EntityEntry.loading();
            entries.put(key, entry);
        }

        isReadOnly(entry);

        entry.save();

        firstLevelCache.put(key, entity);
        snapshots.put(key, EntitySnapshot.from(entity));

        entry.managed();
    }

    @Override
    public void removeEntity(Object entity) {
        EntityMetadata entityMetadata = EntityMetadata.of(entity.getClass(), entity);
        EntityKey key = EntityKey.of(entity.getClass(), entityMetadata.getPrimaryKey().getValue());

        EntityEntry entry = entries.get(key);
        boolean isAlreadyGoneStatus = Objects.isNull(entry);

        if (isAlreadyGoneStatus) {
            return;
        }

        isReadOnly(entry);

        entry.delete();
        firstLevelCache.remove(key);
        snapshots.remove(key);

        entry.gone();
    }

    private static void isReadOnly(EntityEntry entry) {
        if (entry.isReadOnly()) {
            throw new IllegalStateException("Entity is read-only");
        }
    }

    @Override
    public EntitySnapshot getDatabaseSnapshot(Object id, Object entity) {
        return snapshots.computeIfAbsent(
                EntityKey.of(entity.getClass(), id), key -> EntitySnapshot.from(entity));
    }

    @Override
    public EntitySnapshot getCachedDatabaseSnapshot(Object id, Object entity) {
        return snapshots.get(EntityKey.of(entity.getClass(), id));
    }

    public EntityEntry getEntry(Object entity) {
        EntityMetadata entityMetadata = EntityMetadata.of(entity.getClass(), entity);
        EntityKey key = EntityKey.of(entity.getClass(), entityMetadata.getPrimaryKey().getValue());
        return entries.get(key);
    }
}
