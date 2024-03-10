package persistence.sql.dml.model;

import persistence.sql.ColumnUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Value {

    private static final String SEPARATOR = ", ";

    private final Object entity;

    public Value(Object entity) {
        this.entity = entity;
    }

    public String getValueClause() {
        final Class<?> clz = entity.getClass();

        return Arrays.stream(clz.getDeclaredFields())
                .filter(ColumnUtils::includeColumn)
                .map(this::getDatabaseValue)
                .collect(Collectors.joining(SEPARATOR));
    }

    public String getDatabaseValue(Field field) {
        Object value;

        field.setAccessible(true);
        try {
            value = field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        return mappingValue(value);
    }

    private String mappingValue(Object value) {
        if (value instanceof String) {
            return "'" + value + "'";
        }
        return String.valueOf(value);
    }

}
