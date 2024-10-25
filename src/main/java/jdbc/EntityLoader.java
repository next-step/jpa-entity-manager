package jdbc;

import jakarta.persistence.Transient;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import persistence.sql.ddl.ColumnName;

public class EntityLoader<T> implements RowMapper<T> {

    private final Class<T> type;

    public EntityLoader(Class<T> type) {
        this.type = type;
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        try {
            T entity = type.getDeclaredConstructor().newInstance();

            for (Field field : type.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.isAnnotationPresent(Transient.class)) {
                    continue;
                }

                Object value = resultSet.getObject(new ColumnName(field).getColumnName());

                if (value != null) {
                    field.set(entity, value);
                }
            }
            return entity;

        } catch (Exception e) {
            throw new RuntimeException("Error mapping ResultSet to entity", e);
        }
    }

}
