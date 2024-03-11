package persistence;

import jdbc.DefaultRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DMLGenerator;

public class EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final DMLGenerator dmlGenerator;

    public EntityLoader(JdbcTemplate jdbcTemplate, DMLGenerator dmlGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlGenerator = dmlGenerator;
    }

    public <T> T load(Class<T> clazz, Long id) {
        String sql = dmlGenerator.generateFindById(id);
        return jdbcTemplate.queryForObject(sql, new DefaultRowMapper<>(clazz));
    }
}
