package domain;

import jakarta.persistence.Transient;
import java.lang.reflect.Field;
import persistence.sql.common.instance.Values;

public class Snapshot {

    private Object id;
    private Object object;
    private Values values;

    public Snapshot(Object id, Object object) {
        this.id = id;
        this.object = initObject(object);
        this.values = Values.of(object);
    }

    private Object initObject(Object object) {
        Object destination;
        try {
            destination = object.getClass().getDeclaredConstructor().newInstance();
            for (Field field : object.getClass().getDeclaredFields()) {
                if (!field.isAnnotationPresent(Transient.class)) {
                    field.setAccessible(true);
                    field.set(destination, field.get(object));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return destination;
    }

    public Object getId() {
        return id;
    }

    public Object getObject() {
        return object;
    }

    public Class getObjectClass() {
        return object.getClass();
    }

    public Values getValues() {
        return values;
    }
}