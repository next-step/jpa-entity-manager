package persistence.sql.entity;

import jakarta.persistence.Id;
import persistence.sql.EntityLoaderFactory;
import persistence.sql.clause.Clause;
import persistence.sql.context.KeyHolder;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.entity.data.Status;
import persistence.sql.loader.EntityLoader;

import java.lang.reflect.Field;
import java.util.List;

public class EntityEntry {
    private final MetadataLoader<?> loader;
    private Status status;
    private Object entity;
    private Object snapshot;
    private KeyHolder key;

    public EntityEntry(MetadataLoader<?> loader, Status status, Object entity, Object snapshot, KeyHolder key) {
        this.loader = loader;
        this.status = status;
        this.entity = entity;
        this.snapshot = snapshot;
        this.key = key;
    }

    public static EntityEntry newLoadingEntry(Object primaryKey, Class<?> returnType) {
        EntityLoader<?> entityLoader = EntityLoaderFactory.getInstance().getLoader(returnType);
        KeyHolder key = new KeyHolder(returnType, primaryKey);

        return new EntityEntry(entityLoader.getMetadataLoader(),
                Status.LOADING,
                null,
                null,
                key);
    }

    public static EntityEntry newEntry(Object entity, Status status) {
        EntityLoader<?> entityLoader = EntityLoaderFactory.getInstance().getLoader(entity.getClass());
        MetadataLoader<?> loader = entityLoader.getMetadataLoader();

        Object id = Clause.extractValue(loader.getPrimaryKeyField(), entity);
        if (id == null && status != Status.SAVING) {
            throw new IllegalStateException("Primary key must not be null");
        }

        KeyHolder key = new KeyHolder(entity.getClass(), id);

        return new EntityEntry(loader, status, entity, createSnapshot(entity, loader), key);

    }

    @SuppressWarnings("unchecked")
    private static <T> T createSnapshot(T entity, MetadataLoader<?> loader) {
        try {

            Object snapshotEntity = loader.getNoArgConstructor().newInstance();
            for (int i = 0; i < loader.getColumnCount(); i++) {
                Field field = loader.getField(i);
                field.setAccessible(true);
                field.set(snapshotEntity, field.get(entity));
            }

            return (T) snapshotEntity;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to create snapshot entity");
        }
    }

    public KeyHolder getKey() {
        return key;
    }

    public Object getEntity() {
        return entity;
    }

    public void updateStatus(Status status) {
        this.status = status;
    }

    public void updateEntity(Object entity) {
        this.entity = entity;
        if (snapshot == null) {
            snapshot = createSnapshot(entity, loader);
        }
    }

    public void synchronizingSnapshot() {
        if (snapshot == null) {
            snapshot = createSnapshot(entity, loader);
            return;
        }

        loader.getFieldAllByPredicate(field -> !field.isAnnotationPresent(Id.class))
                .forEach(field -> copyFieldValue(field, entity, snapshot));
    }

    private void copyFieldValue(Field field, Object entity, Object origin) {
        try {
            field.setAccessible(true);
            Object value = field.get(entity);
            field.set(origin, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Illegal access to field: " + field.getName());
        }
    }

    public boolean isDirty() {
        if (isNotManagedStatus()) {
            return false;
        }

        if (!(snapshot == null && entity == null) && snapshot == null || entity == null) {
            return true;
        }

        List<Field> fields = loader.getFieldAllByPredicate(field -> {
            Object entityValue = Clause.extractValue(field, entity);
            Object snapshotValue = Clause.extractValue(field, snapshot);

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

    private boolean isNotManagedStatus() {
        return !Status.isManaged(status);
    }

    public Object getSnapshot() {
        return snapshot;
    }

    public Status getStatus() {
        return status;
    }
}
