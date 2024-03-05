package persistence.sql.mapping;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import persistence.sql.QueryException;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ColumnBinder {

    private final ColumnTypeMapper columnTypeMapper;

    public ColumnBinder(ColumnTypeMapper columnTypeMapper) {
        this.columnTypeMapper = columnTypeMapper;
    }

    public List<Column> createColumns(final Class<?> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
                .map(this::createColumn)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Column> createColumns(final Object object) {
        final Class<?> clazz = object.getClass();

        return Arrays.stream(clazz.getDeclaredFields())
                .map(field -> createColumn(field, object))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public Column createColumn(final Field field) {
        if (field.isAnnotationPresent(Transient.class)) {
            return null;
        }

        final String columnName = toColumnName(field);
        final int sqlType = columnTypeMapper.toSqlType(field.getType());

        final Column column = getColumn(field, columnName, sqlType);

        final Id idAnnotation = field.getAnnotation(Id.class);

        if (idAnnotation != null) {
            column.setPk(true);

            final GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
            if (generatedValue != null) {
                column.setStrategy(generatedValue.strategy());
            }
        }

        return column;
    }

    private Column getColumn(final Field field, final String columnName, final int sqlType) {
        final Value value = new Value(field.getType(), sqlType);

        final jakarta.persistence.Column columnAnnotation = field.getAnnotation(jakarta.persistence.Column.class);

        if (columnAnnotation != null) {
            int length = columnAnnotation.length();
            boolean nullable = columnAnnotation.nullable();
            boolean unique = columnAnnotation.unique();

            return new Column(columnName, sqlType, value, length, nullable, unique);
        }

        return new Column(columnName, sqlType, value);
    }

    private Column createColumn(final Field field, final Object object) {
        final Column column = createColumn(field);

        if (Objects.isNull(column)) {
            return null;
        }

        setColumnValue(column, field, object);

        return column;
    }

    public static String toColumnName(final Field field) {
        final jakarta.persistence.Column columnAnnotation = field.getAnnotation(jakarta.persistence.Column.class);

        if (columnAnnotation == null || columnAnnotation.name().isBlank()) {
            return field.getName().toLowerCase();
        }

        return columnAnnotation.name();
    }

    private void setColumnValue(final Column column, final Field field, final Object object) {
        try {
            field.setAccessible(true);
            final Object value = field.get(object);

            final Value valueObject = column.getValue();
            valueObject.setValue(value);
        } catch (IllegalAccessException e) {
            throw new QueryException(column.getName() + " column set value exception");
        }

    }

}
