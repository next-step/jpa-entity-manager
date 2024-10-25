package persistence.sql.meta;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class ColumnIdOption {
    private static final List<GenerationType> ID_GENERATION_FROM_DATABASE_TYPES = List.of(GenerationType.IDENTITY);

    private final boolean isId;
    private final GenerationType generationType;

    public ColumnIdOption(Field field) {
        this.isId = isId(field);
        this.generationType = getGenerationType(field);
    }

    public boolean isId() {
        return isId;
    }

    public GenerationType getGenerationType() {
        return generationType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnIdOption that = (ColumnIdOption) o;
        return isId == that.isId && generationType == that.generationType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(isId, generationType);
    }

    public boolean isGenerationValue() {
        return Objects.nonNull(generationType);
    }

    private boolean isId(Field field) {
        return field.isAnnotationPresent(Id.class);
    }

    private GenerationType getGenerationType(Field field) {
        if (!isId) {
            return null;
        }

        final GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
        if (Objects.isNull(generatedValue)) {
            return null;
        }
        return generatedValue.strategy();
    }


    public boolean isIdGenerationFromDatabase() {
        if (!isGenerationValue()) {
            return false;
        }
        return ID_GENERATION_FROM_DATABASE_TYPES.contains(generationType);
    }
}
