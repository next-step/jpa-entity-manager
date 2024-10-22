package persistence.sql.context.impl;

import jakarta.persistence.Id;
import persistence.sql.context.KeyHolder;
import persistence.sql.context.PersistenceContext;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.dml.impl.SimpleMetadataLoader;

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
    public <T, ID> void add(ID id, T entity) {
        KeyHolder key = new KeyHolder(entity.getClass(), id);
        if (context.containsKey(key)) {
            return;
        }

        context.put(key, entity);
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
        MetadataLoader<?> loader = new SimpleMetadataLoader<>(entity.getClass());
        loader.getFieldAllByPredicate(field -> !field.isAnnotationPresent(Id.class))
                .forEach(field -> copyFieldValue(field, entity, origin));
    }

    private <T> void copyFieldValue(Field field, T entity, Object origin) {
        try {
            field.setAccessible(true);
            Object value = field.get(entity);
            field.set(origin, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Illegal access to field: " + field.getName());
        }
    }
}
