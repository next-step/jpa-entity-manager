package persistence.sql.dml.builder;

import persistence.sql.meta.column.ColumnName;
import persistence.sql.meta.table.TableName;

import java.lang.reflect.Field;
import java.util.Arrays;

public class UpdateQueryBuilder {

    public String generateSQL(Long pk, Object entity) {
        Class<?> clazz = entity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        Arrays.stream(fields)
                .forEach(field -> generateSetSQL(entity, field, sb));
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        TableName tableName = new TableName(entity.getClass());
        return String.format("UPDATE %s SET %s WHERE id = %s",
                tableName.getName(),
                sb,
                pk.toString());
    }

    private void generateSetSQL(Object entity, Field field, StringBuilder sb) {
        try {
            field.setAccessible(true);
            Object value = field.get(entity);
            if (value != null) {
                sb.append(new ColumnName(field).getName())
                        .append(" = '")
                        .append(value)
                        .append("', ");
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }


}
