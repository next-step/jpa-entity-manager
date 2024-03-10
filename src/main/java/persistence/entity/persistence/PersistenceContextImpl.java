package persistence.entity.persistence;

import persistence.entity.domain.EntityEntry;
import persistence.entity.domain.EntityKey;
import persistence.entity.domain.EntitySnapshot;
import persistence.entity.domain.EntityStatus;
import persistence.sql.ddl.domain.Columns;
import persistence.sql.dml.domain.Value;

import java.util.HashMap;
import java.util.Map;

public class PersistenceContextImpl implements PersistenceContext {

    private final Map<EntityKey, Object> entities = new HashMap<>();
    private final Map<EntityKey, EntitySnapshot> snapshots = new HashMap<>();
    private final Map<EntityKey, EntityEntry> entries = new HashMap<>();

    @Override
    public <T> T getEntity(Class<T> clazz, Object id) {
        return (T) entities.get(new EntityKey(clazz, id));
    }

    @Override
    public void addEntity(Object id, Object entity) {
        EntityKey entityKey = new EntityKey(entity.getClass(), id);
        entities.put(entityKey, entity);
        entries.put(entityKey, new EntityEntry(EntityStatus.MANAGED));
    }

    @Override
    public void removeEntity(Object entity) {
        Columns columns = new Columns(entity.getClass());
        EntityKey entityKey = new EntityKey(entity.getClass(), columns.getOriginValue(entity));
        EntityEntry entityEntry = entries.get(entityKey);
        entityEntry.updateStatus(EntityStatus.DELETED);
        entities.remove(entityKey);
        entityEntry.updateStatus(EntityStatus.GONE);
    }

    @Override
    public EntitySnapshot getDatabaseSnapshot(Object id, Object entity) {
        EntityKey entityKey = new EntityKey(entity.getClass(), id);
        return snapshots.computeIfAbsent(entityKey, key -> new EntitySnapshot(entity));
    }

    @Override
    public EntitySnapshot getCachedDatabaseSnapshot(Object id, Object entity) {
        EntityKey entityKey = new EntityKey(entity.getClass(), id);
        return snapshots.get(entityKey);
    }

    @Override
    public void addEntityEntry(Object entity, EntityStatus entityStatus) {
        entries.put(new EntityKey(entity.getClass(), entity), new EntityEntry(entityStatus));
    }

}
