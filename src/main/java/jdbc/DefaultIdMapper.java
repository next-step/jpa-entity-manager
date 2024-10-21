package jdbc;

import jakarta.persistence.Id;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class DefaultIdMapper implements IdMapper {
    private static final Logger logger = LoggerFactory.getLogger(DefaultIdMapper.class);
    private static final String NOT_ID_FAILED_MESSAGE = "필드에 @Id 애노테이션이 없습니다.";

    private final Object entity;

    public DefaultIdMapper(Object entity) {
        this.entity = entity;
    }

    @Override
    public void mapRow(ResultSet resultSet) throws SQLException, IllegalAccessException {
        final Field field = getIdField();
        mapField(resultSet, field);
    }

    private Field getIdField() {
        return Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(NOT_ID_FAILED_MESSAGE));
    }

    private void mapField(ResultSet resultSet, Field field) throws SQLException, IllegalAccessException {
        final Object value = resultSet.getObject(1);
        field.setAccessible(true);
        field.set(entity, value);
    }
}
