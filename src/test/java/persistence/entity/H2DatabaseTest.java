package persistence.entity;

import database.H2;
import database.sql.Person;
import database.sql.ddl.CreateQueryBuilder;
import database.sql.util.type.MySQLTypeConverter;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.sql.SQLException;

abstract public class H2DatabaseTest {
    protected static final MySQLTypeConverter typeConverter = new MySQLTypeConverter();

    protected static H2 server;
    protected JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void startServer() throws SQLException {
        server = new H2();
        server.start();
    }

    @BeforeEach
    void initJdbcTemplate() throws SQLException {
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute("DROP TABLE users IF EXISTS");
        jdbcTemplate.execute(new CreateQueryBuilder(Person.class, typeConverter).buildQuery());
    }

    @AfterAll
    static void shutdownServer() {
        server.stop();
    }

}
