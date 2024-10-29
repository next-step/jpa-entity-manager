package persistence.model.meta;

import java.lang.reflect.Field;

public class Value {
    private Object value = null;

    private Value() {
    }

    public Value(Object value) {
        this.value = value;
    }

    public static Value create(Object entityObject, Field field) {
        field.setAccessible(true);
        try {
            Object value = field.get(entityObject);
            if (value == null) {
                return new Value();
            }
            return new Value(value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getValue() {
        return value;
    }

    public Boolean isNull() {
        return value == null;
    }
}
