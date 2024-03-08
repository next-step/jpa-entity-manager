package persistence.entity;

import database.Database;
import database.DatabaseServer;
import database.H2;
import database.SimpleDatabase;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import persistence.sql.ddl.DDLQueryBuilder;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dml.DMLQueryBuilder;
import persistence.sql.model.Table;
import persistence.study.sql.ddl.Person3;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class EntityLoaderTest {

    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;
    private static EntityLoader loader;

    private static DDLQueryBuilder ddlQueryBuilder;
    private static DMLQueryBuilder dmlQueryBuilder;

    @BeforeAll
    static void initialize() throws SQLException {
        server = new H2();
        server.start();

        Connection jdbcConnection = server.getConnection();
        jdbcTemplate = new JdbcTemplate(jdbcConnection);

        Database database = new SimpleDatabase(jdbcTemplate);
        loader = new EntityLoader(database);

        Dialect dialect = new H2Dialect();
        Table table = new Table(Person3.class);
        ddlQueryBuilder = new DDLQueryBuilder(table, dialect);
        dmlQueryBuilder = new DMLQueryBuilder(table);
    }

    @AfterAll
    static void close() {
        server.stop();
    }

    @BeforeEach
    void setUp() {
        String createTableQuery = ddlQueryBuilder.buildCreateQuery();
        jdbcTemplate.execute(createTableQuery);

        Stream<Person3> persons = createPersons();
        persons.forEach(person -> {
            String insertQuery = dmlQueryBuilder.buildInsertQuery(person);
            jdbcTemplate.execute(insertQuery);
        });
    }

    private Stream<Person3> createPersons() {
        return Stream.of(
                new Person3("qwer1", 1, "email1@email.com"),
                new Person3("qwer2", 2, "email2@email.com"),
                new Person3("qwer3", 3, "email3@email.com")
        );
    }

    @AfterEach
    void setDown() {
        String dropQuery = ddlQueryBuilder.buildDropQuery();
        jdbcTemplate.execute(dropQuery);
    }

    @Test
    @DisplayName("person을 이용하여 read 메서드 테스트")
    void read() {
        EntityId id = new EntityId(3L);
        Person3 result = loader.read(Person3.class, id);

        Person3 expect = new Person3(3L, "qwer3", 3, "email3@email.com");
        assertThat(result).isEqualTo(expect);
    }

    @ParameterizedTest
    @MethodSource
    @DisplayName("person을 이용하여 isExist 메서드 테스트")
    void isExist(Person3 person, boolean expect) {
        boolean result = loader.isExist(person);

        assertThat(result).isEqualTo(expect);
    }

    private static Stream<Arguments> isExist() {
        return Stream.of(
                Arguments.arguments(new Person3(1L, "qwer3", 3, "email3@email.com"), true),
                Arguments.arguments(new Person3(5L, "qwer3", 3, "email3@email.com"), false)
        );
    }
}
