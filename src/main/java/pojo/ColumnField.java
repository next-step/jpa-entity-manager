package pojo;

import jakarta.persistence.Column;

import java.lang.reflect.Field;
import java.util.Objects;

public class ColumnField implements FieldData {

    private final FieldInfo fieldInfo;

    public ColumnField(Field field, Object entity) {
        this.fieldInfo = new FieldInfo(field, entity);
    }

    public String getFieldLength(boolean isVarcharType) {
        return Objects.nonNull(getColumnLength(isVarcharType)) ? "(" + getColumnLength(isVarcharType) + ")" : null;
    }

    public String getColumnNullConstraint() {
        if (!isColumnField() || fieldInfo.getField().getAnnotation(Column.class).nullable()) {
            return Constraints.NULL.getName();
        }
        return Constraints.NOT_NULL.getName();
    }

    private String getColumnLength(boolean isVarcharType) {
        if (isColumnField() && isVarcharType) {
            return String.valueOf(fieldInfo.getField().getAnnotation(Column.class).length());
        }

        if (isColumnField() && !isVarcharType) {
            return getLengthOrDefaultValue(255);
        }

        return null;
    }

    private String getLengthOrDefaultValue(int defaultLengthValue) {
        return fieldInfo.getField().getAnnotation(Column.class).length() == defaultLengthValue ? null
                : String.valueOf(fieldInfo.getField().getAnnotation(Column.class).length());
    }

    @Override
    public boolean isIdField() {
        return false;
    }

    @Override
    public boolean isColumnField() {
        return true;
    }

    @Override
    public boolean isNotTransientField() {
        return true;
    }

    @Override
    public boolean isNullableField() {
        return fieldInfo.getField().getAnnotation(Column.class).nullable();
    }

    @Override
    public String getFieldNameData() {
        return fieldInfo.getFieldName().getName();
    }

    @Override
    public Object getFieldValueData() {
        return fieldInfo.getFieldValue().getValue();
    }
}
