package persistence.entity;

import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.DialectFactory;
import persistence.sql.dml.DmlQueryGenerator;

public class EntityLoader {

    private final JdbcTemplate jdbcTemplate;
    private final DmlQueryGenerator dmlQueryGenerator;

    private EntityLoader(JdbcTemplate jdbcTemplate, DmlQueryGenerator dmlQueryGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryGenerator = dmlQueryGenerator;
    }

    public static EntityLoader of(JdbcTemplate jdbcTemplate) {
        DialectFactory dialectFactory = DialectFactory.getInstance();
        Dialect dialect = dialectFactory.getDialect(jdbcTemplate.getDbmsName());
        DmlQueryGenerator dmlQueryGenerator = DmlQueryGenerator.of(dialect);
        return new EntityLoader(jdbcTemplate, dmlQueryGenerator);
    }

    public <T> T selectOne(Class<T> clazz, Long id) {
        String selectByPkQuery = dmlQueryGenerator.generateSelectByPkQuery(clazz, id);
        return jdbcTemplate.queryForObject(selectByPkQuery, new EntityRowMapper<>(clazz));
    }
}
