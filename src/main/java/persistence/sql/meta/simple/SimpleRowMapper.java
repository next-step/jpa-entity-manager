package persistence.sql.meta.simple;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jdbc.RowMapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleRowMapper <T> implements RowMapper<T> {

    private Class<?> clazz;
    public SimpleRowMapper(Class clazz) {
        this.clazz = clazz;
    }

    @Override
    public T mapRow(final ResultSet resultSet) throws SQLException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        final T instance = (T) clazz.getDeclaredConstructor().newInstance();

        final List<Field> fields = Arrays.stream(clazz.getDeclaredFields())
                .sorted(Comparator.comparing(this::idFirstOrdered))
                .filter(this::isNotTransientField)
                .collect(Collectors.toList());

        for (Field field : fields) {
            field.setAccessible(true);
            field.set(instance, resultSet.getObject(getFieldName(field)));
        }

        return instance;
    }

    private boolean isNotTransientField(final Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }

    private String getFieldName(final Field field) {
        if (isNotBlankOf(field)) {
            return field.getAnnotation(Column.class).name();
        }

        return field.getName();
    }

    private boolean isNotBlankOf(final Field field) {
        return field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).name().isBlank();
    }

    private Integer idFirstOrdered(Field field) {
        return field.isAnnotationPresent(Id.class) ? 0 : 1;
    }
}
