package persistence.context;

import dialect.Dialect;
import dialect.H2Dialect;
import persistence.entity.EntityCacheKey;
import persistence.entity.EntitySnapshot;
import pojo.FieldInfo;
import pojo.FieldInfos;
import pojo.FieldValue;

import java.util.HashMap;
import java.util.Map;

public class SimplePersistenceContext implements PersistenceContext {

    private final Dialect dialect = new H2Dialect();
    private final Map<EntityCacheKey, Object> entitiesByKey = new HashMap<>();
    private final Map<EntityCacheKey, EntitySnapshot> entitySnapshotsByKey = new HashMap<>();

    @Override
    public <T> T getEntity(Class<T> clazz, Object id) {
        Object object = entitiesByKey.get(new EntityCacheKey(clazz, id));
        return clazz.cast(object);
    }

    @Override
    public void addEntity(Object entity) {
        FieldInfo idFieldData = new FieldInfos(entity.getClass().getDeclaredFields()).getIdFieldData();
        FieldValue idFieldValue = new FieldValue(dialect, idFieldData.getField(), entity);
        addEntity(Long.valueOf(String.valueOf(idFieldValue.getValue())), entity);
    }

    @Override
    public void addEntity(Long id, Object entity) {
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity.getClass(), id);
        entitiesByKey.put(entityCacheKey, entity);
        entitySnapshotsByKey.put(entityCacheKey, new EntitySnapshot(dialect, entity));
    }

    @Override
    public void removeEntity(Object entity) {
        FieldInfo idFieldData = new FieldInfos(entity.getClass().getDeclaredFields()).getIdFieldData();
        FieldValue idFieldValue = new FieldValue(dialect, idFieldData.getField(), entity);
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity.getClass(), idFieldValue.getValue());
        entitiesByKey.remove(entityCacheKey);
        entitySnapshotsByKey.remove(entityCacheKey);
    }

    @Override
    public Object getDatabaseSnapshot(Long id, Object entity) {
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity.getClass(), id);
        return entitySnapshotsByKey.computeIfAbsent(entityCacheKey, cacheKey -> new EntitySnapshot(dialect, entity));
    }

    @Override
    public EntitySnapshot getCachedDatabaseSnapshot(Object entity) {
        FieldInfo idFieldData = new FieldInfos(entity.getClass().getDeclaredFields()).getIdFieldData();
        FieldValue idFieldValue = new FieldValue(dialect, idFieldData.getField(), entity);
        return getCachedDatabaseSnapshot(Long.valueOf(String.valueOf(idFieldValue.getValue())), entity);
    }

    @Override
    public EntitySnapshot getCachedDatabaseSnapshot(Long id, Object entity) {
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity.getClass(), id);
        return entitySnapshotsByKey.get(entityCacheKey);
    }
}
