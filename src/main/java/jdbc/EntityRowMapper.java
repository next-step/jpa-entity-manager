package jdbc;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityRowMapper<T> implements RowMapper<T> {

    private final Class<T> clazz;

    public EntityRowMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException {
        try {
            T object = clazz.getDeclaredConstructor().newInstance();
            return createMappedObject(object, resultSet);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    private T createMappedObject(T object, ResultSet resultSet) throws IllegalAccessException, SQLException {
        List<Field> fields = getColumnFields(clazz);
        for (Field field : fields) {
            field.setAccessible(true);
            field.set(object, resultSet.getObject(getColumnName(field)));
        }
        return object;
    }

    private List<Field> getColumnFields(Class<T> clazz) {
        return Arrays.stream(clazz.getDeclaredFields())
            .filter(field -> !field.isAnnotationPresent(Transient.class))
            .collect(Collectors.toList());
    }

    private String getColumnName(Field field) {
        if (field.isAnnotationPresent(Column.class)) {
            return getColumnNameFromAnnotation(field);
        }
        return field.getName();
    }

    private String getColumnNameFromAnnotation(Field field) {
        Column annotation = field.getAnnotation(Column.class);
        String name = annotation.name();
        if (name.isEmpty()) {
            return field.getName();
        }
        return name;
    }
}
