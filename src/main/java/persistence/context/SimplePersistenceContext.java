package persistence.context;

import dialect.Dialect;
import dialect.H2Dialect;
import persistence.entity.EntityCacheKey;
import persistence.entity.EntitySnapshot;
import pojo.FieldInfos;
import pojo.IdField;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class SimplePersistenceContext implements PersistenceContext {

    private final Dialect dialect = new H2Dialect();
    private final Map<EntityCacheKey, Object> entitiesByKey = new HashMap<>();
    private final Map<EntityCacheKey, EntitySnapshot> entitySnapshotsByKey = new HashMap<>();

    @Override
    public <T> T getEntity(Class<T> clazz, Object id) {
        Object entity = entitiesByKey.get(new EntityCacheKey(clazz, id));
        return clazz.cast(entity);
    }

    @Override
    public void addEntity(Object entity) {
        Field field = new FieldInfos(entity.getClass().getDeclaredFields()).getIdField();
        IdField idField = new IdField(field, entity);
        addEntity(Long.valueOf(String.valueOf(idField.getFieldValueData())), entity);
    }

    @Override
    public void addEntity(Long id, Object entity) {
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity.getClass(), id);
        entitiesByKey.put(entityCacheKey, entity);
        entitySnapshotsByKey.put(entityCacheKey, new EntitySnapshot(entity));
    }

    @Override
    public void removeEntity(Object entity) {
        Field field = new FieldInfos(entity.getClass().getDeclaredFields()).getIdField();
        IdField idField = new IdField(field, entity);
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity.getClass(), idField.getFieldValueData());
        entitiesByKey.remove(entityCacheKey);
        entitySnapshotsByKey.remove(entityCacheKey);
    }

    @Override
    public EntitySnapshot getDatabaseSnapshot(Object entity) {
        Field field = new FieldInfos(entity.getClass().getDeclaredFields()).getIdField();
        IdField idField = new IdField(field, entity);
        return getDatabaseSnapshot(Long.valueOf(String.valueOf(idField.getFieldValueData())), entity);
    }

    @Override
    public EntitySnapshot getDatabaseSnapshot(Long id, Object entity) {
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity.getClass(), id);
        return entitySnapshotsByKey.computeIfAbsent(entityCacheKey, cacheKey -> new EntitySnapshot(entity));
    }

    @Override
    public EntitySnapshot getCachedDatabaseSnapshot(Object entity) {
        Field field = new FieldInfos(entity.getClass().getDeclaredFields()).getIdField();
        IdField idField = new IdField(field, entity);
        return getCachedDatabaseSnapshot(Long.valueOf(String.valueOf(idField.getFieldValueData())), entity);
    }

    @Override
    public EntitySnapshot getCachedDatabaseSnapshot(Long id, Object entity) {
        EntityCacheKey entityCacheKey = new EntityCacheKey(entity.getClass(), id);
        return entitySnapshotsByKey.get(entityCacheKey);
    }
}
