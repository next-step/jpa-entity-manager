package persistence.entity.loader;

import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlGenerator;

public class SimpleEntityLoader implements EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final DmlGenerator dmlGenerator;

    private SimpleEntityLoader(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlGenerator = DmlGenerator.getInstance();
    }

    public static SimpleEntityLoader from(JdbcTemplate jdbcTemplate) {
        return new SimpleEntityLoader(jdbcTemplate);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        return jdbcTemplate.queryForObject(dmlGenerator.generateSelectQuery(clazz, id),
            resultSet -> new EntityRowMapper<>(clazz).mapRow(resultSet));
    }
}
