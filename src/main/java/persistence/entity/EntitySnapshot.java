package persistence.entity;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import persistence.exception.NotSameException;

public record EntitySnapshot(Object entity) {

    public EntitySnapshot(Object entity) {
        this.entity = EntityCopyUtils.deepCopy(entity);
    }

    public boolean hasDifferenceWith(Object entity) {
        Class<?> entityType = this.entity.getClass();
        validateEntityType(entity, entityType);

        Field[] fields = entityType.getDeclaredFields();
        return Arrays.stream(fields)
                .anyMatch(field -> isNotSame(
                        getFieldValue(field, this.entity),
                        getFieldValue(field, entity)));
    }

    public List<Field> getDifferenceFields(Object entity) {
        Class<?> entityType = this.entity.getClass();
        validateEntityType(entity, entityType);

        return Arrays.stream(entityType.getDeclaredFields())
                .peek(field -> field.setAccessible(true))
                .filter(field -> isNotSame(getFieldValue(field, this.entity),
                        getFieldValue(field, entity)))
                .collect(Collectors.toList());
    }

    private void validateEntityType(Object entity, Class<?> entityType) {
        if (isNotSameClass(entityType)) {
            throw new NotSameException(
                    MessageFormat.format("EntitySnapshot class: {0}, Entity class: {1}",
                            this.entity.getClass(),
                            entity.getClass()));
        }
    }

    private Object getFieldValue(Field field, Object entity) {
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isSame(Object obj1, Object obj2) {
        return isNotNull(obj1) && obj1.equals(obj2);
    }

    private boolean isNotSameClass(Class<?> entityType) {
        return !isSame(this.entity.getClass(), entityType);
    }

    private boolean isNotSame(Object obj1, Object obj2) {
        return !isSame(obj1, obj2);
    }

    private boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    private boolean isNull(Object obj) {
        return obj == null;
    }

}
