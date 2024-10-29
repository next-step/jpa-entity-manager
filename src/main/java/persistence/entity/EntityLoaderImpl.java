package persistence.entity;

import jdbc.JdbcTemplate;

public class EntityLoaderImpl implements EntityLoader {
    JdbcTemplate jdbcTemplate;

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        return null;
    }
}
