package jdbc;

import jakarta.persistence.Transient;
import persistence.sql.metadata.Column;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class EntityMapper<T> implements RowMapper<T>{
    private final Class<T> clazz;

    public EntityMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException {
        if(!resultSet.next()) {
            return null;
        }

        try {
            T entity = clazz.getDeclaredConstructor().newInstance();

            Field[] fields = Arrays.stream(clazz.getDeclaredFields())
                    .filter(x -> !x.isAnnotationPresent(Transient.class))
                    .toArray(Field[]::new);

            for(Field field : fields) {
                Column column = new Column(field, null);

                field.setAccessible(true);
                field.set(entity, resultSet.getObject(column.getName()));
            }

            return entity;
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
