package persistence.sql.ddl.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.lang.reflect.Field;

public class PkColumn implements Column {

    private final GenerationType generationType;
    private final DefaultColumn defaultColumn;

    public PkColumn(Field field) {
        this.generationType = createGenerationType(field);
        this.defaultColumn = new DefaultColumn(field);
    }

    private GenerationType createGenerationType(Field field) {
        if (field.isAnnotationPresent(GeneratedValue.class)) {
            return field.getAnnotation(GeneratedValue.class).strategy();
        }
        return null;
    }

    @Override
    public boolean isPrimaryKey() {
        return true;
    }

    @Override
    public GenerationType getGenerationType() {
        return generationType;
    }

    @Override
    public boolean isNotAutoIncrementId() {
        return !GenerationType.IDENTITY.equals(generationType);
    }

    @Override
    public Field getField() {
        return defaultColumn.getField();
    }

    @Override
    public String getName() {
        return defaultColumn.getName();
    }

    @Override
    public Type getType() {
        return defaultColumn.getType();
    }

    @Override
    public String getLength() {
        return defaultColumn.getLength();
    }

    @Override
    public boolean isNotNull() {
        return defaultColumn.isNotNull();
    }

}
