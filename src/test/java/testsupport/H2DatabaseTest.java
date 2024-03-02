package testsupport;

import database.H2;
import database.dialect.Dialect;
import database.dialect.MySQLDialect;
import database.sql.Person;
import database.sql.ddl.CreateQueryBuilder;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.Connection;
import java.sql.SQLException;

abstract public class H2DatabaseTest {
    protected static final Dialect dialect = MySQLDialect.INSTANCE;

    protected static H2 server;
    protected Connection connection;
    protected LoggingJdbcTemplate loggingJdbcTemplate;

    @BeforeAll
    static void startServer() throws SQLException {
        server = new H2();
        server.start();
    }

    @BeforeEach
    void initJdbcTemplate() throws SQLException {
        connection = server.getConnection();

        createTable();

        loggingJdbcTemplate = new LoggingJdbcTemplate(connection);
    }

    private void createTable() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
        jdbcTemplate.execute("DROP TABLE users IF EXISTS");
        jdbcTemplate.execute(new CreateQueryBuilder(Person.class, dialect).buildQuery());
    }

    @AfterAll
    static void shutdownServer() {
        server.stop();
    }

}
