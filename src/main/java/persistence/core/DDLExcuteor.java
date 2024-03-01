package persistence.core;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import persistence.sql.ddl.DDLQueryBuilder;
import persistence.sql.ddl.DDLQueryBuilderFactory;

import java.sql.SQLException;

public class DDLExcuteor {

    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private DDLQueryBuilder ddlQueryBuilder;

    public DDLExcuteor() throws SQLException {
        this.server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        ddlQueryBuilder = DDLQueryBuilderFactory.getDDLQueryBuilder(server);
    }

    public void createTable(Class<?> clazz) {
        String sql = ddlQueryBuilder.createTableQuery(clazz);
        jdbcTemplate.execute(sql);
        stopServer();
    }

    public void dropTable(Class<?> clazz) {
        String sql = ddlQueryBuilder.dropTableQuery(clazz);
        jdbcTemplate.execute(sql);
        stopServer();
    }

    private void stopServer() {
        server.stop();
    }

}
