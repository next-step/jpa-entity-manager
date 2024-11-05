package persistence.entity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import persistence.exception.NotSameException;

public record EntitySnapshot(Object entity) {

    public EntitySnapshot(Object entity) {
        this.entity = entity;
    }

    public Object compare(Object entity) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> snapshotEntityType = this.entity.getClass();

        if (notSameClass(snapshotEntityType)) {
            throw new NotSameException(
                    MessageFormat.format("EntitySnapshot class: {0}, Entity class: {1}",
                            this.entity.getClass(),
                            entity.getClass())
            );
        }

        Object diffObject = snapshotEntityType.getDeclaredConstructor().newInstance();
        Field[] fields = snapshotEntityType.getDeclaredFields();
        for (Field field : fields) {
            setDifferentField(field, entity, diffObject);
        }

        return diffObject;
    }

    private void setDifferentField(Field field, Object entity, Object diffObject) throws IllegalAccessException {
        field.setAccessible(true);

        Object value1 = field.get(this.entity);
        Object value2 = field.get(entity);

        if (isNull(value1) || !value1.equals(value2)) {
            field.set(diffObject, value2);
        }
    }

    private boolean isNull(Object entity) {
        return entity == null;
    }

    private boolean notSameClass(Class<?> entityType) {
        return !this.entity.getClass().equals(entityType);
    }

}
