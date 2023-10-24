package persistence.entity;

import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.DialectFactory;
import persistence.sql.dml.DmlQueryGenerator;

public class DefaultEntityManager implements EntityManager {

    private final JdbcTemplate jdbcTemplate;
    private final DmlQueryGenerator dmlQueryGenerator;

    private DefaultEntityManager(JdbcTemplate jdbcTemplate, DmlQueryGenerator dmlQueryGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryGenerator = dmlQueryGenerator;
    }

    public static DefaultEntityManager of(JdbcTemplate jdbcTemplate) {
        DialectFactory dialectFactory = DialectFactory.getInstance();
        Dialect dialect = dialectFactory.getDialect(jdbcTemplate.getDbmsName());
        DmlQueryGenerator dmlQueryGenerator = DmlQueryGenerator.of(dialect);
        return new DefaultEntityManager(jdbcTemplate, dmlQueryGenerator);
    }

    @Override
    public <T> T find(Class<T> clazz, Long id) {
        String selectByPkQuery = dmlQueryGenerator.generateSelectByPkQuery(clazz, id);
        return jdbcTemplate.queryForObject(selectByPkQuery, new EntityRowMapper<>(clazz));
    }

    @Override
    public void persist(Object entity) {
        String insertEntityQuery = dmlQueryGenerator.generateInsertQuery(entity);
        jdbcTemplate.execute(insertEntityQuery);
    }

    @Override
    public void remove(Object entity) {
        String deleteEntityQuery = dmlQueryGenerator.generateDeleteQuery(entity);
        jdbcTemplate.execute(deleteEntityQuery);
    }
}
