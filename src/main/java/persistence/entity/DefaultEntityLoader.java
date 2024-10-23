package persistence.entity;

import jdbc.DefaultRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.SelectQueryBuilder;

public class DefaultEntityLoader implements EntityLoader {
    private final JdbcTemplate jdbcTemplate;
    private final SelectQueryBuilder selectQueryBuilder;

    public DefaultEntityLoader(JdbcTemplate jdbcTemplate, SelectQueryBuilder selectQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.selectQueryBuilder = selectQueryBuilder;
    }

    @Override
    public <T> T load(Class<T> entityType, Object id) {
        final String sql = selectQueryBuilder.findById(entityType, id);
        return jdbcTemplate.queryForObject(sql, new DefaultRowMapper<>(entityType));
    }
}
