package persistence.entity;

import jakarta.persistence.Transient;
import jdbc.RowMapper;
import persistence.sql.mapping.ColumnData;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultRowMapper<T> implements RowMapper<T> {
    private final Class<T> clazz;
    private final List<Field> fields;

    public DefaultRowMapper(Class<T> clazz) {
        this.clazz = clazz;
        this.fields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .collect(Collectors.toList());
    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException {
        T entity;
        try {
            entity = clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new SQLException(e);
        }

        for (Field field : fields) {
            ColumnData columnData = ColumnData.createColumn(field);
            setValue(entity, field, resultSet.getObject(columnData.getName()));
        }
        return entity;
    }

    private void setValue(T entity, Field field, Object value) throws SQLException {
        field.setAccessible(true);
        try {
            field.set(entity, value);
        } catch (IllegalAccessException e) {
            throw new SQLException(e);
        }
    }
}
