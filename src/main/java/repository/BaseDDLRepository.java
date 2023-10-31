package repository;

import jdbc.JdbcTemplate;
import persistence.dialect.Dialect;
import persistence.sql.QueryGenerator;

public class BaseDDLRepository<T, ID> extends AbstractRepository<T, ID> implements DDLRepository<T> {

    protected BaseDDLRepository(JdbcTemplate jdbcTemplate, Class<T> tClass, Dialect dialect) {
        super(jdbcTemplate, tClass, dialect);
    }

    public void createTable() {
        final String sql = QueryGenerator.of(entityMeta, dialect).create();
        jdbcTemplate.execute(sql);
    }

    public void dropTable() {
        final String sql = QueryGenerator.of(entityMeta, dialect).drop();
        jdbcTemplate.execute(sql);
    }
}
