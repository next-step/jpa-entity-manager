package persistence.sql.entity;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

class CacheEntry {
    private final Object entity;
    private final Map<String, Object> snapshot;

    CacheEntry(final Object entity) {
        this.entity = entity;
        this.snapshot = captureSnapshot(entity);
    }

    private Map<String, Object> captureSnapshot(final Object entity) {
        final Map<String, Object> values = new HashMap<>();
        for (final Field field : entity.getClass().getDeclaredFields()) {
            if (isPersistentField(field)) {
                field.setAccessible(true);
                captureFieldValue(entity, field, values);
            }
        }
        return values;
    }

    private boolean isPersistentField(final Field field) {
        return !field.isAnnotationPresent(Id.class) &&
               !field.isAnnotationPresent(Transient.class);
    }

    private void captureFieldValue(final Object entity, final Field field, final Map<String, Object> values) {
        try {
            values.put(field.getName(), field.get(entity));
        } catch (final IllegalAccessException e) {
            throw new RuntimeException("Failed to capture field value: " + field.getName(), e);
        }
    }

    boolean isDirty() {
        for (final Field field : entity.getClass().getDeclaredFields())
            if (isPersistentField(field) && isFieldDirty(field)) return true;
        return false;
    }

    private boolean isFieldDirty(final Field field) {
        try {
            field.setAccessible(true);
            final Object currentValue = field.get(entity);
            final Object snapshotValue = snapshot.get(field.getName());
            return !Objects.equals(currentValue, snapshotValue);
        } catch (final IllegalAccessException e) {
            throw new RuntimeException("Failed to check if field is dirty: " + field.getName(), e);
        }
    }

    Object getEntity() {
        return entity;
    }
}
