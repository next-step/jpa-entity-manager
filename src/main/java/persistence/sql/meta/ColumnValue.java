package persistence.sql.meta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Objects;

public class ColumnValue {
    private static final Logger logger = LoggerFactory.getLogger(ColumnValue.class);

    private final Class<?> type;
    private final Object value;
    private final boolean isQuotesNeeded;

    public ColumnValue(Field field) {
        this.type = field.getType();
        this.value = null;
        this.isQuotesNeeded = false;
    }

    public ColumnValue(Field field, Object entity) {
        this.type = field.getType();
        this.value = value(field, entity);
        this.isQuotesNeeded = isQuotesNeeded();
    }

    public Object value() {
        return value;
    }

    public String valueWithQuotes() {
        if (isQuotesNeeded) {
            return "'%s'".formatted(String.valueOf(value));
        }
        return String.valueOf(value);
    }

    private Object value(Field field, Object entity) {
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    private boolean isQuotesNeeded() {
        return type == String.class;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnValue that = (ColumnValue) o;
        return isQuotesNeeded == that.isQuotesNeeded && Objects.equals(type, that.type) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value, isQuotesNeeded);
    }
}
