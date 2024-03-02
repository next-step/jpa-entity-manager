package persistence.entity;

import jakarta.persistence.Transient;
import jdbc.RowMapper;
import persistence.sql.mapping.ColumnData;
import persistence.sql.mapping.Columns;
import persistence.sql.mapping.DataTypeMapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DynamicRowMapperFactory {
    public <T> RowMapper<T> create(Class<T> clazz) {
        List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> !field.isAnnotationPresent(Transient.class))
                .collect(Collectors.toList());

        return rs -> {
            try {
                T entity = clazz.getConstructor().newInstance();
                setEntityValue(fields, rs, entity);
                return entity;
            } catch (Exception e) {
                throw new SQLException(e);
            }
        };
    }

    private <T> void setEntityValue(List<Field> fields, ResultSet rs, T entity) throws IllegalAccessException, SQLException {
        for (Field field : fields) {
            ColumnData columnData = ColumnData.createColumn(field);
            field.setAccessible(true);
            field.set(entity, rs.getObject(columnData.getName()));
        }
    }
}
