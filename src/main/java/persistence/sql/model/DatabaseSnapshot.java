package persistence.sql.model;

import org.jetbrains.annotations.NotNull;

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
}
