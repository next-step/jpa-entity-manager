package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.DialectFactory;
import persistence.sql.dml.DmlQueryGenerator;

public class EntityPersister {

    private static final boolean PROCESSED_SUCCESS = true;

    private final JdbcTemplate jdbcTemplate;
    private final DmlQueryGenerator dmlQueryGenerator;

    private EntityPersister(JdbcTemplate jdbcTemplate, DmlQueryGenerator dmlQueryGenerator) {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryGenerator = dmlQueryGenerator;
    }

    public static EntityPersister of(JdbcTemplate jdbcTemplate) {
        DialectFactory dialectFactory = DialectFactory.getInstance();
        Dialect dialect = dialectFactory.getDialect(jdbcTemplate.getDbmsName());
        DmlQueryGenerator dmlQueryGenerator = DmlQueryGenerator.of(dialect);
        return new EntityPersister(jdbcTemplate, dmlQueryGenerator);
    }

    public void insert(Object entity) {
        String insertEntityQuery = dmlQueryGenerator.generateInsertQuery(entity);
        jdbcTemplate.execute(insertEntityQuery);
    }

    public boolean update(Object entity) {
        String updateEntityQuery = dmlQueryGenerator.generateUpdateQuery(entity);
        jdbcTemplate.execute(updateEntityQuery);
        return PROCESSED_SUCCESS;
    }

    public void delete(Object entity) {
        String deleteEntityQuery = dmlQueryGenerator.generateDeleteQuery(entity);
        jdbcTemplate.execute(deleteEntityQuery);
    }

}
