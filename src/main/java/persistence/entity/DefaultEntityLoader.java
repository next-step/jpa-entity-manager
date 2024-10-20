package persistence.entity;

import jdbc.DefaultRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.SelectQueryBuilder;

public class DefaultEntityLoader implements EntityLoader {
    private final JdbcTemplate jdbcTemplate;

    public DefaultEntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> T find(Class<T> entityType, Object id) {
        final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder(entityType);
        final String sql = selectQueryBuilder.findById(id);
        return jdbcTemplate.queryForObject(sql, new DefaultRowMapper<>(entityType));
    }
}
