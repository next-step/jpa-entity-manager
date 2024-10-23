package persistence.sql.context.impl;

import jakarta.persistence.Id;
import persistence.sql.EntityLoaderFactory;
import persistence.sql.context.KeyHolder;
import persistence.sql.context.PersistenceContext;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.dml.impl.SimpleMetadataLoader;
import persistence.sql.loader.EntityLoader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DefaultPersistenceContext implements PersistenceContext {
    private final Map<KeyHolder, Object> context = new HashMap<>();
    private final Map<KeyHolder, Object> snapshot = new HashMap<>();

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

        context.put(key, entity);
        createSnapshot(key, entity);
    }

    @Override
    public <T> void delete(T entity) {
        KeyHolder key = new KeyHolder(entity.getClass(), entity);
        context.remove(key);
        snapshot.remove(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, ID> T getDatabaseSnapshot(ID id, T entity) {
        KeyHolder key = new KeyHolder(entity.getClass(), entity);
        Object snapshotEntity = snapshot.get(key);

        if (snapshotEntity != null) {
            return (T) snapshotEntity;
        }

        return null;
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

    private <T> void createSnapshot(KeyHolder key, T entity) {
        try {
            EntityLoader<?> entityLoader = EntityLoaderFactory.getInstance().getLoader(entity.getClass());
            MetadataLoader<?> loader = entityLoader.getMetadataLoader();

            Object snapshotEntity = loader.getNoArgConstructor().newInstance();
            for (int i = 0; i < loader.getColumnCount(); i++) {
                Field field = loader.getField(i);
                field.setAccessible(true);
                field.set(snapshotEntity, field.get(entity));
            }

            snapshot.put(key, snapshotEntity);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to create snapshot entity");
        }
    }
}
