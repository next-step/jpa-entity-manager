package persistence.sql.entity;

import jakarta.persistence.Column;
import jdbc.JdbcTemplate;

import java.lang.reflect.Field;
import java.sql.Connection;

public class EntityLoader {

    private final JdbcTemplate jdbcTemplate;

    public EntityLoader(Connection connection) {
        this.jdbcTemplate = new JdbcTemplate(connection);
    }

    public <T> T loadEntity(Class<T> clazz, String query) {
        return jdbcTemplate.queryForObject(query, new EntityRowMapper<>(clazz));
    }

    public void load(String query) {
         jdbcTemplate.execute(query);
    }

    private String getColumnName(Field field) {
        Column annotation = field.getAnnotation(Column.class);
        if (annotation == null) {
            return field.getName();
        }
        if (!annotation.name().isEmpty()) {
            return annotation.name();
        }

        return field.getName();
    }
}
