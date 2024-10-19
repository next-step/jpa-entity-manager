package jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DefaultRowMapper<T> implements RowMapper<T> {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRowMapper.class);

    private final Class<T> clazz;

    public DefaultRowMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        final T entity = new InstanceFactory<>(clazz).createInstance();
        final Field[] fields = clazz.getDeclaredFields();

        for (int i = 0; i < fields.length; i++) {
            mapField(resultSet, entity, fields[i], i + 1);
        }
        return entity;
    }

    private void mapField(ResultSet resultSet, T entity, Field field, int columnIndex) {
        try {
            final Object value = resultSet.getObject(columnIndex);
            field.setAccessible(true);
            field.set(entity, value);
        } catch (SQLException | IllegalAccessException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
