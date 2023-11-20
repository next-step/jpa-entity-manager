package persistence.common;

import java.lang.reflect.Field;

public class FieldValue {
    private FieldClazz fieldClazz;
    private String value;

    public FieldValue(Field field, String value) {
        this.fieldClazz = new FieldClazz(field);
        this.value = value;
    }

    public FieldValue(FieldClazz fieldClazz, String value) {
        this.fieldClazz = fieldClazz;
        this.value = value;
    }

    public String getFieldName() {
        return fieldClazz.getName();
    }

    public String getValue() {
        return value;
    }

    public Class<?> getClazz() {
        return this.fieldClazz.getClazz();
    }

    public boolean isId() {
        return fieldClazz.isId();
    }
}
