package persistence.sql.context.impl;

import persistence.sql.context.KeyHolder;
import persistence.sql.context.PersistenceContext;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DefaultPersistenceContext implements PersistenceContext {
    private final Map<KeyHolder, Object> context = new HashMap<>();

    @Override
    public <T, ID> T get(Class<T> entityType, ID id) {
        KeyHolder key = new KeyHolder(entityType, id);

        if (context.containsKey(key)) {
            return entityType.cast(context.get(key));
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, ID> T add(ID id, T entity) {
        KeyHolder key = new KeyHolder(entity.getClass(), id);
        if (context.containsKey(key)) {
            return (T) context.get(key);
        }

        return (T) context.put(key, entity);
    }

    @Override
    public <T, ID> void merge(ID id, T entity) {
        KeyHolder key = new KeyHolder(entity.getClass(), id);
        Object origin = context.get(key);
        if (origin == null) {
            add(id, entity);
            return;
        }

        if (!isEntityChanged(entity, origin)) {
            return;
        }

        overwriteEntity(entity, origin);
    }

    @Override
    public <T> void delete(T entity) {
        KeyHolder key = new KeyHolder(entity.getClass(), entity);
        context.remove(key);
    }

    private boolean isEntityChanged(Object entity, Object snapshot) {
        return !entity.equals(snapshot);
    }

    private <T> void overwriteEntity(T entity, Object origin) {
        Field[] declaredFields = entity.getClass().getDeclaredFields();

        for (Field field : declaredFields) {
            field.setAccessible(true);
            try {
                Object value = field.get(entity);
                field.set(origin, value);
            } catch (IllegalAccessException e) {
                throw new IllegalStateException("Illegal access to field: " + field.getName());
            }
        }
    }
}
