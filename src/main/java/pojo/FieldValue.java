package pojo;

import dialect.Dialect;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 필드 value, 값
 */
public class FieldValue {

    private final Dialect dialect;
    private final String value;

    public FieldValue(Dialect dialect, Field field, Object object) {
        this.dialect = dialect;
        this.value = getFieldValue(field, object);
    }

    public String getValue() {
        return value;
    }

    private String getFieldValue(Field field, Object object) {
        field.setAccessible(true);
        try {
            FieldInfo fieldInfo = new FieldInfo(field);
            Object fieldValue = field.get(object);

            if (Objects.isNull(fieldValue)) {
                checkFieldValidation(fieldInfo);
                return null;
            }

            String typeToStr = dialect.getTypeToStr(field.getType());
            return typeToStr.equals("varchar") ? "'" + fieldValue + "'" : String.valueOf(fieldValue);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("필드 정보를 가져올 수 없습니다.");
        }
    }

    private void checkFieldValidation(FieldInfo fieldInfo) {
        if (fieldInfo.isIdField() && !fieldInfo.isValidIdField()) {
            throw new IllegalArgumentException("fieldValue 가 null 이어서는 안됩니다.");
        }

        if (fieldInfo.isColumnField() && !fieldInfo.isNullableField()) {
            throw new IllegalArgumentException("fieldValue 가 null 이어서는 안됩니다.");
        }
    }
}
