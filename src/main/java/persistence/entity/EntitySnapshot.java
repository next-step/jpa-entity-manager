package persistence.entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import persistence.exception.NotSameException;

public class EntitySnapshot {

    private final Object entity;

    public EntitySnapshot(Object entity) {
        this.entity = entity;
    }

    public Object compare(Object entity) {
        Class<?> entityType = this.entity.getClass();
        if (notSameClass(entityType)) {
            throw new NotSameException(MessageFormat.format("EntitySnapshot class: {0}, Entity class: {1}",
                            this.entity.getClass(),
                            entity.getClass()));
        }

        Object diffObject = getNewInstance();
        Field[] fields = entityType.getDeclaredFields();
        for (Field field : fields) {
            setDifferentField(field, entity, diffObject);
        }

        return diffObject;
    }

    private Object getNewInstance() {
        try {
            Class<?> entityType = this.entity.getClass();
            return entityType.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void setDifferentField(Field field, Object entity, Object diffObject) {
        field.setAccessible(true);

        Object value1 = getFieldValue(field, this.entity);
        Object value2 = getFieldValue(field, entity);

        setFieldValue(field, diffObject, value1, value2);
    }

    private void setFieldValue(Field field, Object diffObject, Object value1, Object value2) {
        if (isSame(value1, value2)) {
            return;
        }

        try {
            field.set(diffObject, value2);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getFieldValue(Field field, Object entity) {
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean notSameClass(Class<?> entityType) {
        return !isSame(this.entity.getClass(), entityType);
    }

    private boolean isSame(Object obj1, Object obj2) {
        return !obj1.equals(obj2);
    }

}
