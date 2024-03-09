package jdbc;

import persistence.sql.ddl.domain.Column;
import persistence.sql.ddl.domain.Columns;

import java.lang.reflect.Field;
import java.sql.ResultSet;

public class GenericRowMapper<T> implements RowMapper<T> {

    private final Class<T> clazz;

    public GenericRowMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T mapRow(final ResultSet resultSet) {
        try {
            Columns columns = new Columns(clazz);
            return toInstance(resultSet, columns);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private T toInstance(ResultSet resultSet, Columns columns) throws Exception {
        T instance = clazz.getDeclaredConstructor().newInstance();

        for (Column column : columns.getColumns()) {
            Field field = column.getField();
            field.setAccessible(true);
            field.set(instance, resultSet.getObject(column.getName()));
        }
        return instance;
    }

}
