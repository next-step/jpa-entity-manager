package persistence.sql.column;

import persistence.sql.dialect.Dialect;
import persistence.sql.type.NameType;
import persistence.sql.type.NullableType;

import java.lang.reflect.Field;

public class GeneralColumn implements Column {

    private static final String DEFAULT_COLUMN_FORMAT = "%s %s";
    private static final String QUOTES = "'";

    private final NameType name;
    private Object value;
    private final ColumnType columnType;
    private NullableType nullable;

    public GeneralColumn(Field field, Dialect dialect) {
        this.columnType = dialect.getColumn(field.getType());
        this.nullable = new NullableType();
        String columnName = getColumnNameWithColumn(field);
        this.name = new NameType(field.getName(), columnName);
    }

    public GeneralColumn(Object object, Field field, Dialect dialect) {
        this(field, dialect);
        this.value = getValue(object, field);
    }

    private Object getValue(Object entity, Field field) {
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String getColumnNameWithColumn(Field field) {
        String columnName = field.getName();
        if (field.isAnnotationPresent(jakarta.persistence.Column.class)) {
            boolean isNullable = field.getAnnotation(jakarta.persistence.Column.class).nullable();
            this.nullable = new NullableType(isNullable);
            columnName = field.getAnnotation(jakarta.persistence.Column.class).name();
        }
        return columnName;
    }

    @Override
    public String getDefinition() {
        return String.format(DEFAULT_COLUMN_FORMAT, name.getValue(),
                columnType.getColumnDefinition() + nullable.getDefinition());
    }

    @Override
    public String getName() {
        return name.getValue();
    }

    @Override
    public String getFieldName() {
        return name.getFieldName();
    }

    public Object getValue() {
        if (value instanceof String) {
            return QUOTES + value + QUOTES;
        }
        return value;
    }
}
