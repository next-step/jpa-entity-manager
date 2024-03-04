package persistence.core;

import jdbc.DefaultRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DMLQueryBuilder;

import java.util.List;

public class DefaultEntityLoader implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final DMLQueryBuilder dmlQueryBuilder;

    public DefaultEntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryBuilder = DMLQueryBuilder.getInstance();
    }

    @Override
    public <T> List<T> find(Class<T> clazz) throws Exception {
        String sql = dmlQueryBuilder.selectAllSql(clazz);

        return jdbcTemplate .query(sql, new DefaultRowMapper<>(clazz));
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) throws Exception {
        String sql = dmlQueryBuilder.selectByIdQuery(clazz, id);

        return jdbcTemplate.queryForObject(sql, new DefaultRowMapper<>(clazz));
    }
}
