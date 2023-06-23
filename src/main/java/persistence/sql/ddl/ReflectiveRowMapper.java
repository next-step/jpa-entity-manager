package persistence.sql.ddl;

import jakarta.persistence.Column;
import jakarta.persistence.Transient;
import jdbc.RowMapper;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class ReflectiveRowMapper<T> implements RowMapper<T> {
    private final Class<T> targetType;

    public ReflectiveRowMapper(Class<T> targetType) {
        this.targetType = targetType;
    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException {
        T targetObject = null;
        try {
            targetObject = targetType.getDeclaredConstructor().newInstance();

            Field[] fields = fields(targetType.getDeclaredFields());
            for (Field field : fields) {
                String columnName = columnName(field);
                Object value = resultSet.getObject(columnName);
                field.setAccessible(true);
                field.set(targetObject, value);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return targetObject;
    }

    private Field[] fields(Field[] fields) {
        return Arrays.stream(fields)
                .filter(field -> field.getAnnotation(Transient.class) == null)
                .toArray(Field[]::new);
    }

    private String columnName(Field field) {
        Column columnAnnotation = field.getAnnotation(Column.class);

        if (columnAnnotation == null) {
            return field.getName();
        }

        if (columnAnnotation.name().equals("")) {
            return field.getName();
        }

        return columnAnnotation.name();
    }
}
