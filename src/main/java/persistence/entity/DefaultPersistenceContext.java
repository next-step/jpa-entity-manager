package persistence.entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import persistence.meta.EntityColumn;
import persistence.meta.EntityMeta;

public class DefaultPersistenceContext implements PersistenceContext {
    private final Map<Object, Object> context = new ConcurrentHashMap<>();
    private final Map<Object, Object> snapShotContext = new ConcurrentHashMap<>();
    private final EntityMeta entityMeta;

    public DefaultPersistenceContext(EntityMeta entityMeta) {
        this.entityMeta = entityMeta;
    }

    @Override
    public Object getEntity(Object id) {
        return context.get(id);
    }

    @Override
    public void addEntity(Object id, Object entity) {
        context.put(id, entity);
    }

    @Override
    public void removeEntity(Object entity) {
        context.remove(entityMeta.getPkValue(entity));
    }

    @Override
    public Object getDatabaseSnapshot(Object id, Object entity) {
        return snapShotContext.put(id, makeSnapShot(entity));
    }

    @Override
    public List<Object> getChangedEntity() {
        return context.entrySet().stream()
                        .filter(it -> !it.getValue().equals(snapShotContext.get(it.getKey())))
                        .map(Map.Entry::getValue)
                        .collect(Collectors.toList());
    }

    private Object makeSnapShot(Object entity) {
        try {
            Object snapShot = entity.getClass().getDeclaredConstructor().newInstance();
            copyColumns(entity, snapShot);
            return snapShot;
        } catch (InstantiationException | NoSuchMethodException | NoSuchFieldException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private void copyColumns(Object entity, Object snapShot) throws NoSuchFieldException, IllegalAccessException {
        for (EntityColumn entityColumn : entityMeta.getEntityColumns()) {
            final String fieldName = entityColumn.getFieldName();
            final Object value = entityColumn.getFieldValue(entity);
            final Field declaredField = entity.getClass().getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            declaredField.set(snapShot, value);
        }
    }

    @Override
    public Object getCachedDatabaseSnapshot(Object id) {
        return snapShotContext.get(id);
    }

    @Override
    public void clear() {
        context.clear();
        snapShotContext.clear();
    }
}
