package persistence.sql.context.impl;

import persistence.sql.context.PersistenceContext;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DefaultPersistenceContext implements PersistenceContext {
    private final Map<Class<?>, Map<?, Object>> context = new HashMap<>();

    @Override
    public <T, ID> T get(Class<T> entityType, ID id) {
        if (context.containsKey(entityType) && context.get(entityType).containsKey(id)) {
            return entityType.cast(context.get(entityType).get(id));
        } else {
            return null;
        }
    }

    @Override
    public <T> Collection<T> getAll(Class<T> entityType) {
        if (context.containsKey(entityType)) {
            return context.get(entityType).values().stream()
                    .map(entityType::cast).toList();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, ID> T add(ID id, T entity) {
        Map<ID, Object> entityMap = (Map<ID, Object>) context.computeIfAbsent(entity.getClass(), k -> new HashMap<>());

        if (entityMap.containsKey(id)) {
            return (T) entityMap.get(id);
        }

        return (T) entityMap.put(id, entity);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, ID> void merge(ID id, T entity) {
        Map<ID, Object> entityMap = (Map<ID, Object>) context.computeIfAbsent(entity.getClass(), k -> new HashMap<>());

        Object origin = entityMap.get(id);
        if (origin == null || !isEntityChanged(entity, origin)) {
            return;
        }

        overwriteEntity(entity, origin);
    }

    @Override
    public <T> void delete(T entity) {

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
