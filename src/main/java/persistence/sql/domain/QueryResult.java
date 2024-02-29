package persistence.sql.domain;

import jdbc.RowMapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QueryResult<T> implements RowMapper<T> {

    private final DatabaseTable table;

    private final Class<T> clazz;

    public QueryResult(DatabaseTable table, Class<T> clazz) {
        this.table = table;
        this.clazz = clazz;
    }

    private void setEntityFieldValue(T entity, ColumnOperation column, ResultSet resultSet) {
        String jdbcColumnName = column.getJdbcColumnName();
        String javaFieldName = column.getJavaFieldName();
        try {
            Object value = resultSet.getObject(jdbcColumnName);
            Field field = clazz.getDeclaredField(javaFieldName);
            field.setAccessible(true);
            field.set(entity, value);
        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        try {
            T entity = clazz.getConstructor().newInstance();
            table.getAllColumns().forEach(column -> setEntityFieldValue(entity, column, resultSet));
            return entity;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("fail to map row", e);
        }
    }
}
