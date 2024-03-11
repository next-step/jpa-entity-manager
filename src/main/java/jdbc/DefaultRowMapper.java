package jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.ddl.column.ColumnName;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Arrays;

public class DefaultRowMapper<T> implements RowMapper<T> {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRowMapper.class);

    private final Class<T> aClass;

    public DefaultRowMapper(Class<T> aClass) {
        this.aClass = aClass;
    }

    @Override
    public T mapRow(ResultSet resultSet) {
        try {
            T entity = aClass.getDeclaredConstructor().newInstance();
            ResultSetMetaData metaData = resultSet.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                Object value = resultSet.getObject(i);
                String columnName = metaData.getColumnName(i);

                Field field = getField(columnName);

                field.setAccessible(true);
                field.set(entity, value);
            }
            return entity;
        } catch (Exception e) {
            logger.error("not work RowMapper", e);
        }
        return null;
    }

    private Field getField(String columnName) {
        return Arrays.stream(aClass.getDeclaredFields())
                .filter(f -> ColumnName.from(f)
                        .getName()
                        .equals(columnName.toLowerCase()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("not found field"));
    }
}
