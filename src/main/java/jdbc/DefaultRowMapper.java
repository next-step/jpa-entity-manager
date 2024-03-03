package jdbc;

import persistence.core.EntityContextManager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DefaultRowMapper<T> implements RowMapper<T> {

    private final Class<T> mappedClass;

    public DefaultRowMapper(Class<T> mappedClass) {
        this.mappedClass = mappedClass;
    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
        T object = mappedClass.getDeclaredConstructor().newInstance();
        int columnCount = resultSet.getMetaData().getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            String columnName = resultSet.getMetaData().getColumnName(i).toLowerCase();
            setField(object, columnName, resultSet);
        }

        return object;
    }

    private void setField(Object object, String columnName, ResultSet resultSet) throws SQLException, IllegalAccessException {
        Field field = EntityContextManager.getEntityMetadata(object.getClass()).getColumns().getColumnByColumnName(columnName).getField();
        field.setAccessible(true);
        field.set(object, resultSet.getObject(columnName));
    }


}
