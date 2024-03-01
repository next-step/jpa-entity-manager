package persistence;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.*;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dml.InsertQueryBuilder;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.dml.WhereBuilder;

import java.util.List;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class H2DBTestSupport {
    protected static JdbcTemplate jdbcTemplate;
    private static DatabaseServer server;
    @BeforeAll
    public static void tearUp() throws Exception {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
    }

    @AfterAll
    public static void tearDown() {
        server.stop();
    }
}
