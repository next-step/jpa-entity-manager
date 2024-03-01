package jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import persistence.sql.meta.Column;
import persistence.sql.meta.Table;

public class EntityRowMapper<T> implements RowMapper {

    private final Class<T> clazz;

    public EntityRowMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        try {
            T t = clazz.getDeclaredConstructor().newInstance();
            Table table = Table.getInstance(clazz);
            for (Column column : table.getColumns()) {
                column.setFieldValue(t, ResultSetColumnReader.map(resultSet, column.getColumnName(), column.getType()));
            }
            return t;
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
