package persistence.entity;

import database.Database;
import database.DatabaseServer;
import database.H2;
import database.SimpleDatabase;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.*;
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
    private static EntityPersister persister;

    private static DDLQueryBuilder ddlQueryBuilder;
    private static DMLQueryBuilder dmlQueryBuilder;

    @BeforeAll
    static void initialize() throws SQLException {
        server = new H2();
        server.start();

        Connection jdbcConnection = server.getConnection();
        jdbcTemplate = new JdbcTemplate(jdbcConnection);

        Database database = new SimpleDatabase(jdbcTemplate);
        EntityMetaCache entityMetaCache = new EntityMetaCache();
        persister = new EntityPersister(database, entityMetaCache);

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
        Person3 result = persister.read(Person3.class, 3L);

        Person3 expect = new Person3(3L, "qwer3", 3, "email3@email.com");
        assertThat(result).isEqualTo(expect);
    }
}
