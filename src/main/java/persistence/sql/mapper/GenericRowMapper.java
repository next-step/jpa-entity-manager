package persistence.sql.mapper;

import jdbc.RowMapper;
import persistence.exception.CanNotCreateInstance;
import persistence.exception.CanNotFindDeclaredConstructorException;
import persistence.exception.CanNotGetObjectException;
import persistence.sql.column.Column;
import persistence.sql.column.Columns;
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
        } catch (InvocationTargetException | IllegalAccessException| InstantiationException e) {
            throw new CanNotCreateInstance("[ERROR] 리플렉션으로 인스턴스를 만들 수 없습니다.", e);
        } catch (NoSuchMethodException e) {
            throw new CanNotFindDeclaredConstructorException("[ERROR] 리플렉션으로 생성자를 찾을 수 없습니다", e);
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

    private void setColumnValue(ResultSet resultSet, T instance, Column column) {
        String fieldName = column.getFieldName();
        String idColumnName = column.getName();

        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new CanNotFindDeclaredConstructorException("[ERROR] 리플렉션으로 생성자를 찾을 수 없습니다", e);
        }
        field.setAccessible(true);
        try {
            field.set(instance, resultSet.getObject(idColumnName));
        } catch (IllegalAccessException | SQLException e) {
            throw new CanNotGetObjectException("[ERROR] field의 값을 불러오는데 실패했습니다.", e);
        }
    }
}
