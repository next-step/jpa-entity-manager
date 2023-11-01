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
        try {
            Object destination = object.getClass().getDeclaredConstructor().newInstance();
            for (Field field : object.getClass().getDeclaredFields()) {
                if (!field.isAnnotationPresent(Transient.class)) {
                    field.setAccessible(true);
                    field.set(destination, field.get(object));
                }
            }
            return destination;
        } catch (Exception e) {
            e.printStackTrace(); // 예외 처리 추가
            return null;
        }
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
