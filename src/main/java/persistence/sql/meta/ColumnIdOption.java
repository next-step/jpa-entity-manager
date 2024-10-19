package persistence.sql.meta;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.Objects;

public class ColumnIdOption {
    private final boolean isId;
    private final boolean isGenerationValue;

    public ColumnIdOption(Field field) {
        this.isId = isId(field);
        this.isGenerationValue = isGenerationValue(field);
    }

    public boolean isId() {
        return isId;
    }

    public boolean isGenerationValue() {
        return isGenerationValue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnIdOption that = (ColumnIdOption) o;
        return isId == that.isId && isGenerationValue == that.isGenerationValue;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isId, isGenerationValue);
    }

    private boolean isId(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    private boolean isGenerationValue(Field field) {
        if (!isId) {
            return false;
        }

        final GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
        if (Objects.isNull(generatedValue)) {
            return false;
        }
        return generatedValue.strategy() == GenerationType.IDENTITY;
    }
}
