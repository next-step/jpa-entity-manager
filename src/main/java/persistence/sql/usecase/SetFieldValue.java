package persistence.sql.usecase;

import persistence.sql.vo.DatabaseField;

import java.lang.reflect.Field;

public class SetFieldValue {
    public void execute(Object object, DatabaseField databaseField, Object value) {
        if(object == null) {
            throw new NullPointerException("object should not be null");
        }
        setFieldValue(object, databaseField.getOriginalFieldName(), value);
    }

    private void setFieldValue(Object object, String fieldName, Object value) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (NoSuchFieldException | SecurityException | IllegalAccessException e) {
            throw new RuntimeException("field 값 세팅 실패 " + fieldName + " value : " + value);
        }
    }
}
