package persistence.sql.model;

import jpa.EntityInfo;
import org.jetbrains.annotations.NotNull;
import persistence.sql.exception.CouldNotAccessField;
import persistence.sql.exception.ExceptionMessage;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class DatabaseSnapshot {

    private final Object entity;

    public DatabaseSnapshot(Object entity) {
        this.entity = entity;
    }

    public Object getEntity() {
        try {
            Object object = makeDefaultConstructor();
            injectFields(object);
            return object;
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private Object makeDefaultConstructor()
            throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?> defaultConstructor = this.entity.getClass().getConstructor();
        defaultConstructor.setAccessible(true);
        return defaultConstructor.newInstance();
    }

    private void injectFields(Object object) throws IllegalAccessException {
        Field[] fields = this.entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            field.set(object, field.get(this.entity));
        }
    }

    public boolean isDirty(Object managedEntity) {
        if (managedEntity == null || this.entity == null) {
            return false;
        }

        if (isNotEqualFields(managedEntity, this.entity)) {
            return true;
        }

        return false;
    }

    private boolean isNotEqualFields(Object managedEntity, Object snapshotEntity) {
        for (Field field : managedEntity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object managedValue = field.get(managedEntity);
                Object snapshotValue = field.get(snapshotEntity);
                if (!managedValue.equals(snapshotValue)) {
                    return true;
                }
            } catch (IllegalAccessException e) {
                throw new CouldNotAccessField(e, ExceptionMessage.COULD_NOT_ACCESS_FIELD);
            }
        }
        return false;
    }
}
