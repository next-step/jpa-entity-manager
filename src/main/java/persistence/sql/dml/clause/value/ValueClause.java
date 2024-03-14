package persistence.sql.dml.clause.value;

import java.lang.reflect.Field;

public class ValueClause {
    public static final String APOSTROPHE = "'";
    private final String value;
    public ValueClause(Field field, Object entity) {
        this.value = getValue(field, entity);
    }

    private static String getValue(Field field, Object entity) {
        field.setAccessible(true);
        Object value;
        try {
            value = field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("value 생성에 실패하였습니다.", e);
        }
        if (isString(field, value)) {
            return APOSTROPHE + value + APOSTROPHE;
        }
        return String.valueOf(value);
    }

    private static boolean isString(Field field, Object value) {
        return String.class.isAssignableFrom(field.getType()) && value != null;
    }

    public String value() {
        return this.value;
    }
}
