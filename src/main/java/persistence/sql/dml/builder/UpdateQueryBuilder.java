package persistence.sql.dml.builder;

import persistence.sql.meta.column.ColumnName;
import persistence.sql.meta.table.TableName;

import java.lang.reflect.Field;
import java.util.Arrays;

public class UpdateQueryBuilder {

    public String generateSQL(Long pk, Object oldEntity, Object newEntity) {
        Class<?> clazz = oldEntity.getClass();
        Field[] fields = clazz.getDeclaredFields();
        StringBuilder sb = new StringBuilder();
        Arrays.stream(fields)
                .forEach(field -> generateSetSQL(oldEntity, newEntity, field, sb));
        TableName tableName = new TableName(oldEntity.getClass());
        return String.format("UPDATE %s SET %s WHERE id = %s",
                tableName.getName(),
                sb,
                pk.toString());
    }

    private void generateSetSQL(Object oldEntity, Object newEntity, Field field, StringBuilder sb) {
        try {
            field.setAccessible(true);
            Object findValue = field.get(oldEntity);
            Object newValue = field.get(newEntity);
            if (findValue != newValue) {
                sb.append(new ColumnName(field).getName())
                        .append("='")
                        .append(newValue.toString())
                        .append("'");
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }


}
