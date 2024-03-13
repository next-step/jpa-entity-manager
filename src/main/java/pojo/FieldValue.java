package pojo;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 필드 value, 값
 */
public class FieldValue {

    private final String value;

    public FieldValue(Field field, Object entity) {
        this.value = getFieldValue(field, entity);
    }

    public String getValue() {
        return value;
    }

    private String getFieldValue(Field field, Object entity) {
        field.setAccessible(true);
        try {
            Object fieldValue = field.get(entity);

            if (field.isAnnotationPresent(Id.class) && Objects.isNull(fieldValue)) {
                checkIdFieldValidation(field, entity);
            }

            if (field.isAnnotationPresent(Column.class) && Objects.isNull(fieldValue)) {
                checkColumnFieldValidation(field, entity);
            }

            return field.getType().equals(String.class) ? "'" + fieldValue + "'" : String.valueOf(fieldValue);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("필드 정보를 가져올 수 없습니다.");
        }
    }

    private void checkIdFieldValidation(Field field, Object entity) {
        IdField idField = new IdField(field, entity);
        if (idField.isGenerationTypeAutoOrIdentity()) {
            throw new IllegalArgumentException("fieldValue 가 null 이어서는 안됩니다.");
        }
    }

    private void checkColumnFieldValidation(Field field, Object entity) {
        ColumnField columnField = new ColumnField(field, entity);
        if (!columnField.isNullableField()) {
            throw new IllegalArgumentException("fieldValue 가 null 이어서는 안됩니다.");
        }
    }
}
