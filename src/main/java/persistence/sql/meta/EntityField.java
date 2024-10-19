package persistence.sql.meta;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class EntityField {
    private static final Logger logger = LoggerFactory.getLogger(EntityField.class);

    private final List<Class<?>> quotesNeededTypes = List.of(String.class);

    private final ColumnName columnName;
    private final ColumnLength columnLength;
    private final ColumnIdOption columnIdOption;
    private final ColumnOption columnOption;

    private final Field field;

    public EntityField(Field field) {
        this.field = field;
        this.columnName = new ColumnName(field);
        this.columnLength = new ColumnLength(field);
        this.columnIdOption = new ColumnIdOption(field);
        this.columnOption = new ColumnOption(field);
    }

    public String getColumnName() {
        return columnName.getName();
    }

    public int getColumnLength() {
        return columnLength.getLength();
    }

    public boolean isId() {
        return columnIdOption.isId();
    }

    public boolean isGenerationValue() {
        return columnIdOption.isGenerationValue();
    }

    public boolean isNotNull() {
        return columnOption.isNotNull();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityField that = (EntityField) o;
        return Objects.equals(field, that.field);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(field);
    }

    public String getValue(Object entity) {
        try {
            field.setAccessible(true);
            final String value = String.valueOf(field.get(entity));
            if (isQuotesNeeded()) {
                return String.format("'%s'", value);
            }
            return value;
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public void setValue(Object entity, Object value){
        try {
            field.setAccessible(true);
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }
    }

    public Class<?> getType() {
        return field.getType();
    }

    private boolean isQuotesNeeded() {
        return quotesNeededTypes.contains(field.getType());
    }

}
