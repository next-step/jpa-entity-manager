package persistence.core;

import jdbc.JdbcTemplate;
import persistence.sql.dml.DMLQueryBuilder;

import java.sql.SQLException;

public class DefaultEntityPersister implements EntityPersister {
    private final JdbcTemplate jdbcTemplate;
    private final DMLQueryBuilder dmlQueryBuilder;


    public DefaultEntityPersister(JdbcTemplate jdbcTemplate) throws SQLException {
        this.jdbcTemplate = jdbcTemplate;
        this.dmlQueryBuilder = DMLQueryBuilder.getInstance();
    }

    @Override
    public Long insert(Object entity) {
        String sql = dmlQueryBuilder.insertSql(entity);
        return jdbcTemplate.execute(sql);
    }

    @Override
    public boolean update(Object entity) throws Exception {
        String sql = dmlQueryBuilder.updateSql(entity);
        jdbcTemplate.execute(sql);

        return true;
    }

    @Override
    public void delete(Object entity) throws Exception {
        String sql = dmlQueryBuilder.deleteSql(entity);
        jdbcTemplate.execute(sql);
    }

}
