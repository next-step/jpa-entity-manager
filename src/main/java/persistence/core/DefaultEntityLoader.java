package persistence.core;

import jdbc.DefaultRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DMLQueryBuilder;

import java.util.List;

public class DefaultEntityLoader implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final DMLQueryBuilder dmlQueryBuilder;

    public DefaultEntityLoader(JdbcTemplate jdbcTemplate, DMLQueryBuilder dmlQueryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryBuilder = dmlQueryBuilder;
    }

    @Override
    public <T> List<T> find(Class<T> clazz) throws Exception {
        String sql = dmlQueryBuilder.selectAllSql(clazz);

        return jdbcTemplate .query(sql, new DefaultRowMapper<T>((Class<T>) clazz));
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) throws Exception {
        String sql = dmlQueryBuilder.selectByIdQuery(clazz, id);

        return jdbcTemplate.queryForObject(sql, new DefaultRowMapper<T>((Class<T>) clazz));
    }
}
