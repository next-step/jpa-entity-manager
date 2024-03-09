package domain.pojo;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Objects;

import static domain.pojo.Constraints.NOT_NULL;
import static domain.pojo.Constraints.NULL;

public class FieldInfo {

    private final Field field;

    public FieldInfo(Field field) {
        if (Objects.isNull(field)) {
            throw new IllegalArgumentException("field 가 null 이어서는 안됩니다.");
        }
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    public boolean isIdField() {
        return field.isAnnotationPresent(Id.class);
    }

    public boolean isColumnField() {
        return field.isAnnotationPresent(Column.class);
    }

    public boolean isNotTransientField() {
        return !field.isAnnotationPresent(Transient.class);
    }

    public String getFieldLength(boolean isVarcharType) {
        return Objects.nonNull(getColumnLength(isVarcharType)) ? "(" + getColumnLength(isVarcharType) + ")" : null;
    }

    public String getGenerationTypeStrategy() {
        H2GenerationType generationType = getGenerationType();
        return Objects.nonNull(generationType) ? generationType.getStrategy() : null;
    }

    public String getColumnNullConstraint() {
        if (!isColumnField() || field.getAnnotation(Column.class).nullable()) {
            return NULL.getName();
        }
        return NOT_NULL.getName();
    }

    public boolean isValidIdField() {
        return isGenerationTypeAutoOrIdentity();
    }

    public boolean isNullableField() {
        return field.getAnnotation(Column.class).nullable();
    }

    private String getColumnLength(boolean isVarcharType) {
        if (isColumnField() && isVarcharType) {
            return String.valueOf(field.getAnnotation(Column.class).length());
        }

        if (isColumnField() && !isVarcharType) {
            return getLengthOrDefaultValue(255);
        }

        return null;
    }

    private String getLengthOrDefaultValue(int defaultLengthValue) {
        return field.getAnnotation(Column.class).length() == defaultLengthValue ? null
                : String.valueOf(field.getAnnotation(Column.class).length());
    }

    private boolean isGenerationTypeAutoOrIdentity() {
        H2GenerationType generationType = getGenerationType();
        return Objects.nonNull(generationType) && (generationType.equals(H2GenerationType.AUTO)
                || generationType.equals(H2GenerationType.IDENTITY));
    }

    private H2GenerationType getGenerationType() {
        if (field.isAnnotationPresent(GeneratedValue.class)) {
            return H2GenerationType.from(field.getAnnotation(GeneratedValue.class).strategy());
        }
        return null;
    }
}
