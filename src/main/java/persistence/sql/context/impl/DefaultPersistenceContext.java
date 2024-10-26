package persistence.sql.context.impl;

import jakarta.persistence.Id;
import org.jetbrains.annotations.NotNull;
import persistence.sql.EntityLoaderFactory;
import persistence.sql.clause.Clause;
import persistence.sql.context.KeyHolder;
import persistence.sql.context.PersistenceContext;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.dml.impl.SimpleMetadataLoader;
import persistence.sql.loader.EntityLoader;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

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
    }

    @Override
    public <T, ID> void delete(T entity, ID id) {
        KeyHolder key = new KeyHolder(entity.getClass(), id);
        context.remove(key);
        snapshot.remove(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T, ID> T getDatabaseSnapshot(ID id, T entity) {
        KeyHolder key = new KeyHolder(entity.getClass(), id);
        Object snapshotEntity = snapshot.get(key);

        if (snapshotEntity != null) {
            return (T) snapshotEntity;
        }

        return null;
    }

    @Override
    public <T, ID> void createDatabaseSnapshot(ID id, T entity) {
        KeyHolder key = new KeyHolder(entity.getClass(), id);
        createSnapshot(key, entity);
    }

    @Override
    public <T> void updateSnapshot(Object id, T entity) {
        KeyHolder key = new KeyHolder(entity.getClass(), id);
        Object snapshotEntity = snapshot.get(key);

        if (snapshotEntity != null) {
            overwriteEntity(entity, snapshotEntity);
        }
    }

    @Override
    public boolean isDirty() {
        return context.entrySet().stream()
                .anyMatch(dirtyFilteringPredicate());
    }

    @Override
    public List<Object> getDirtyEntities() {
        return context.entrySet().stream()
                .filter(dirtyFilteringPredicate())
                .map(Map.Entry::getValue)
                .toList();
    }

    @NotNull
    private Predicate<Map.Entry<KeyHolder, Object>> dirtyFilteringPredicate() {
        return entry -> {
            KeyHolder key = entry.getKey();
            Object entity = entry.getValue();
            Object snapshotEntity = snapshot.get(key);

            return snapshotEntity == null || isDirty(entity, snapshotEntity);
        };
    }

    private boolean isDirty(Object entity, Object snapshotEntity) {
        EntityLoader<?> entityLoader = EntityLoaderFactory.getInstance().getLoader(entity.getClass());
        MetadataLoader<?> loader = entityLoader.getMetadataLoader();

        List<Field> fields = loader.getFieldAllByPredicate(field -> {
            Object entityValue = Clause.extractValue(field, entity);
            Object snapshotValue = Clause.extractValue(field, snapshotEntity);

            if (entityValue == null && snapshotValue == null) {
                return false;
            }

            if (entityValue == null || snapshotValue == null) {
                return true;
            }

            return !entityValue.equals(snapshotValue);
        });

        return !fields.isEmpty();
    }

    @Override
    public void cleanup() {
        context.clear();
        snapshot.clear();
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
