package persistence.sql.dml;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class InsertQueryBuilder extends DmlQueryBuilder {

    public InsertQueryBuilder(Class<?> entityClass) {
        super(entityClass);
    }

    public String build(Object entity) {
        Class<?> entityClass = entity.getClass();
        return new StringBuilder()
            .append(String.format("INSERT INTO %s", getTableName(entityClass)))
            .append(columnsClause(entityClass))
            .append(valueClause(entity))
            .toString();
    }

    private String columnsClause(Class<?> entityClass) {
        String columns = Arrays.stream(entityClass.getDeclaredFields())
            .filter(field -> !field.isAnnotationPresent(Transient.class) && !field.isAnnotationPresent(Id.class))
            .map(this::getColumnName)
            .collect(Collectors.joining(", "));
        return String.format(" ( %s ) ", columns);
    }

    private String valueClause(Object entity) {
        String values = Arrays.stream(entity.getClass().getDeclaredFields())
            .filter(field -> !field.isAnnotationPresent(Transient.class) && !field.isAnnotationPresent(Id.class))
            .map(field -> getFieldValue(entity, field))
            .collect(Collectors.joining(", "));
        return String.format("VALUES ( %s )", values);
    }

    private String getFieldValue(Object entity, Field field) {
        try {
            field.setAccessible(true);
            return convertFieldValue(entity, field);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String convertFieldValue(Object entity, Field field) throws IllegalAccessException {
        Object value = field.get(entity);
        if (field.getType().equals(String.class)) {
            return String.format("'%s'", value);
        }
        return String.valueOf(value);
    }
}
