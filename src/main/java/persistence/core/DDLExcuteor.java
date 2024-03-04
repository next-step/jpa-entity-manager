package persistence.core;

import jdbc.JdbcTemplate;
import database.DatabaseVendor;
import persistence.sql.ddl.DDLQueryBuilder;
import persistence.sql.ddl.DDLQueryBuilderFactory;

import java.sql.SQLException;

public class DDLExcuteor {
    private final JdbcTemplate jdbcTemplate;
    private DDLQueryBuilder ddlQueryBuilder;

    public DDLExcuteor(JdbcTemplate jdbcTemplate) throws SQLException {
        this.jdbcTemplate = jdbcTemplate;
        ddlQueryBuilder = DDLQueryBuilderFactory.getDDLQueryBuilder(DatabaseVendor.H2);
    }

    public void createTable(Class<?> clazz) {
        String sql = ddlQueryBuilder.createTableQuery(clazz);
        jdbcTemplate.execute(sql);
    }

    public void dropTable(Class<?> clazz) {
        String sql = ddlQueryBuilder.dropTableQuery(clazz);
        jdbcTemplate.execute(sql);
    }

}
