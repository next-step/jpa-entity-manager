package persistence.sql.mapper;

import jdbc.RowMapper;
import persistence.sql.column.Column;
import persistence.sql.column.Columns;
import persistence.sql.column.GeneralColumn;
import persistence.sql.column.IdColumn;
import persistence.sql.dialect.Dialect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class GenericRowMapper<T> implements RowMapper<T> {
    private final Class<T> clazz;
    private final Dialect dialect;

    public GenericRowMapper(Class<T> clazz, Dialect dialect) {
        this.clazz = clazz;
        this.dialect = dialect;
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            mapIdColumn(resultSet, instance);
            mapGeneralColumns(resultSet, instance);
            return instance;
        } catch (InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private void mapIdColumn(ResultSet resultSet, T instance) {
        IdColumn idColumn = new IdColumn(clazz.getDeclaredFields(), dialect);
        setColumnValue(resultSet, instance, idColumn);
    }

    private void mapGeneralColumns(ResultSet resultSet, T instance) {
        Columns columns = new Columns(clazz.getDeclaredFields(), dialect);
        columns.getValues()
                .forEach(column -> setColumnValue(resultSet, instance, column));
    }

    private void setColumnValue(ResultSet resultSet, T instance, Column idColumn) {
        String fieldName = idColumn.getFieldName();
        String idColumnName = idColumn.getName();

        Field idField = null;
        try {
            idField = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        idField.setAccessible(true);
        try {
            idField.set(instance, resultSet.getObject(idColumnName));
        } catch (IllegalAccessException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
