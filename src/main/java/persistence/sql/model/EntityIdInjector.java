package persistence.sql.model;

import jakarta.persistence.Id;
import persistence.sql.exception.CouldNotAccessField;
import persistence.sql.exception.ExceptionMessage;
import persistence.sql.exception.RequiredIdException;

import java.lang.reflect.Field;
import java.util.Arrays;

public class EntityIdInjector {

    private EntityIdInjector() {
    }

    private static class EntityIdInjectorHolder {
        public static final EntityIdInjector INSTANCE = new EntityIdInjector();
    }

    public static EntityIdInjector getInstance() {
        return EntityIdInjectorHolder.INSTANCE;
    }

    public <T> T inject(T entity, Object id) {
        Field idField = getIdField(entity.getClass());

        try {
            idField.setAccessible(true);
            idField.set(entity, idField.getType().cast(id));
        } catch (IllegalAccessException e) {
            throw new CouldNotAccessField(e, ExceptionMessage.COULD_NOT_ACCESS_FIELD);
        }

        return entity;
    }

    private Field getIdField(Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst().orElseThrow(() -> new RequiredIdException(ExceptionMessage.REQUIRED_ID));
    }

}
