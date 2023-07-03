package persistence.sql.dml;

import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

public class UpdateQueryBuilder extends DmlQueryBuilder {

    public UpdateQueryBuilder(Class<?> entityClass) {
        super(entityClass);
    }

    public String build(Long id, Object entity) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("UPDATE %s ", getTableName(entityClass)));
        builder.append(String.format("SET %s", createColumns(entity)));
        builder.append(whereClause(id));
        return builder.toString();
    }

    private String createColumns(Object entity) {
        return Arrays.stream(entityClass.getDeclaredFields())
            .filter(field -> !field.isAnnotationPresent(Id.class) && !field.isAnnotationPresent(Transient.class))
            .map(field -> String.format("%s = %s", getColumnName(field), getFieldValue(entity, field)))
            .collect(Collectors.joining(", "));
    }

    private Object getFieldValue(Object entity, Field field) {
        try {
            field.setAccessible(true);
            if (field.getType().equals(String.class)) {
                return String.format("'%s'", field.get(entity));
            }
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
