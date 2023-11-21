package persistence.entity.context;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import persistence.sql.usecase.CreateSnapShotObject;
import persistence.sql.usecase.GetFieldFromClass;
import persistence.sql.usecase.GetFieldValue;
import persistence.sql.usecase.GetIdDatabaseField;
import persistence.sql.vo.DatabaseField;

public class PersistenceContextImpl implements PersistenceContext {
    private final Map<Class<?>, Map<Long, Object>> cacheMap = new ConcurrentHashMap<>();
    private final Map<Class<?>, Map<Long, Object>> snapShotMap = new ConcurrentHashMap<>();
    private final GetIdDatabaseField getIdDatabaseField = new GetIdDatabaseField(new GetFieldFromClass());
    private final GetFieldValue getFieldValue = new GetFieldValue();
    private final CreateSnapShotObject createSnapShotObject = new CreateSnapShotObject();

    public PersistenceContextImpl() {
    }

    @Override
    public Object getEntity(Class<?> cls, Long id) {
        Map<Long, Object> cache = getCache(cls);
        return cache.get(id);
    }


    @Override
    public void addEntity(Long id, Object entity) {
        if (id == null || entity == null) {
            return;
        }
        Class<?> cls = entity.getClass();
        Map<Long, Object> cache = getCache(cls);
        cache.put(id, entity);
        Map<Long, Object> snapShot = getSnapShot(cls);
        snapShot.put(id, createSnapShotObject.execute(entity));
    }

    @Override
    public void removeEntity(Object entity) {
        DatabaseField databaseField = getIdDatabaseField.execute(entity.getClass());
        Long id = (Long) getFieldValue.execute(entity, databaseField);
        Class<?> cls = entity.getClass();
        Map<Long, Object> cache = getCache(cls);
        cache.remove(id);
        Map<Long, Object> snapShot = getSnapShot(cls);
        snapShot.remove(id);
    }

    @Override
    public Object getDatabaseSnapshot(Class<?> cls, Long id) {
        Map<Long, Object> snapShot = getSnapShot(cls);
        return snapShot.get(id);
    }

    @Override
    public void clear() {
        cacheMap.forEach((key, value) -> {
            value.clear();
        });
        snapShotMap.forEach((key, value) -> {
            value.clear();
        });
        cacheMap.clear();
        snapShotMap.clear();
    }

    private Map<Long, Object> getCache(Class<?> cls) {
        if (cacheMap.containsKey(cls)) {
            return cacheMap.get(cls);
        }
        Map<Long, Object> cache = new ConcurrentHashMap<>();
        cacheMap.put(cls, cache);
        return cache;
    }

    private Map<Long, Object> getSnapShot(Class<?> cls) {
        if (snapShotMap.containsKey(cls)) {
            return snapShotMap.get(cls);
        }
        Map<Long, Object> snapShot = new ConcurrentHashMap<>();
        snapShotMap.put(cls, snapShot);
        return snapShot;
    }
}
