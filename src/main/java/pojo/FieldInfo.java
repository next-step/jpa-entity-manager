package pojo;

import java.lang.reflect.Field;
import java.util.Objects;

public class FieldInfo {

    private final Field field;
    private final FieldName fieldName;
    private final FieldValue fieldValue;

    public FieldInfo(Field field, Object entity) {
        if (Objects.isNull(field)) {
            throw new IllegalArgumentException("field 가 null 이어서는 안됩니다.");
        }
        this.field = field;
        this.fieldName = new FieldName(field);
        this.fieldValue = new FieldValue(field, entity);
    }

    public Field getField() {
        return field;
    }

    public FieldName getFieldName() {
        return fieldName;
    }

    public FieldValue getFieldValue() {
        return fieldValue;
    }

    public boolean isNotBlankOrEmpty() {
        return Objects.nonNull(fieldName) && Objects.nonNull(fieldValue);
    }

    public String joinNameAndValueWithDelimiter(String delimiter) {
        return String.join(delimiter, fieldName.getName(), String.valueOf(fieldValue.getValue()));
    }
}
