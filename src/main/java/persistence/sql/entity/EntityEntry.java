package persistence.sql.entity;

import jakarta.persistence.Id;
import persistence.sql.EntityLoaderFactory;
import persistence.sql.clause.Clause;
import persistence.sql.context.EntityPersister;
import persistence.sql.context.KeyHolder;
import persistence.sql.context.PersistenceContext;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.dml.impl.SimpleMetadataLoader;
import persistence.sql.entity.data.Status;
import persistence.sql.loader.EntityLoader;

import java.lang.reflect.Field;
import java.util.List;

public class EntityEntry {
    private final MetadataLoader<?> loader;
    private final EntityPersister entityPersister;
    private final PersistenceContext persistenceContext;
    private Status status;
    private Object entity;
    private Object snapshot;
    private KeyHolder key;

    public EntityEntry(PersistenceContext persistenceContext,
                       EntityPersister entityPersister,
                       MetadataLoader<?> loader,
                       Status status,
                       Object entity,
                       Object snapshot,
                       KeyHolder key) {
        this.persistenceContext = persistenceContext;
        this.status = status;
        this.entity = entity;
        this.snapshot = snapshot;
        this.key = key;
        this.entityPersister = entityPersister;
        this.loader = loader;
    }

    public static EntityEntry newEntry(EntityPersister entityPersister, PersistenceContext persistenceContext, Object entity, Status status) {
        EntityLoader<?> entityLoader = EntityLoaderFactory.getInstance().getLoader(entity.getClass());
        MetadataLoader<?> loader = entityLoader.getMetadataLoader();

        Object id = Clause.extractValue(loader.getPrimaryKeyField(), entity);
        if (id == null) {
            throw new IllegalArgumentException("Primary key must not be null");
        }

        KeyHolder key = new KeyHolder(entity.getClass(), id);

        return new EntityEntry(persistenceContext, entityPersister, loader, status, entity, createSnapshot(entity, loader), key);

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
    }

    public void dirtyCheck() {
        if (!isManagedStatus()) {
            return;
        }

        if (status == Status.DELETED) {
            entityPersister.delete(entity);
            persistenceContext.deleteEntry(entity, key.key());
            updateStatus(Status.GONE);
            return;
        }

        if (status == Status.SAVING) {
            entityPersister.insert(entity);
            updateStatus(Status.MANAGED);
            return;
        }

        if (!isDirty()) {
            return;
        }

        entityPersister.update(entity, snapshot);
        synchronizingSnapshot();
    }

    private void synchronizingSnapshot() {
        loader.getFieldAllByPredicate(field -> !field.isAnnotationPresent(Id.class))
                .forEach(field -> copyFieldValue(field, entity, snapshot));
    }

    private  void copyFieldValue(Field field, Object entity, Object origin) {
        try {
            field.setAccessible(true);
            Object value = field.get(entity);
            field.set(origin, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Illegal access to field: " + field.getName());
        }
    }

    private boolean isManagedStatus() {
        return Status.isManaged(status);
    }

    public boolean isDirty() {
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
}
