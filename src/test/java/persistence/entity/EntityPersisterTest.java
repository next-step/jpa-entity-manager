package persistence.entity;

import database.*;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityPersisterTest {

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
        persister = new EntityPersister(database);

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
    @DisplayName("person을 이용하여 create 메서드 테스트")
    void create() {
        Person3 person3 = new Person3("qwer", 12, "qwe@ema.com");

        EntityId id = persister.create(person3);
        Person3 result = jdbcTemplate.queryForObject("SELECT * FROM users WHERE id=4L", new EntityRowMapper<>(Person3.class));

        Person3 expectPerson = new Person3(4L, "qwer", 12, "qwe@ema.com");
        EntityId expectId = new EntityId(4L);
        assertSoftly(softly -> {
            softly.assertThat(result).isEqualTo(expectPerson);
            softly.assertThat(id).isEqualTo(expectId);
        });
    }

    @Test
    void update() {
        Person3 person = new Person3(2L, "qwer", 12, "qwe@ema.com");

        EntityId id = persister.update(person);
        Person3 result = jdbcTemplate.queryForObject("SELECT * FROM users WHERE id=2", new EntityRowMapper<>(Person3.class));

        Person3 expectPerson = new Person3(2L, "qwer", 12, "qwe@ema.com");
        EntityId expectId = new EntityId(2L);
        assertSoftly(softly -> {
            softly.assertThat(result).isEqualTo(expectPerson);
            softly.assertThat(id).isEqualTo(expectId);
        });
    }

    @Test
    void deleteById() {
        Person3 person = new Person3(2L, "aa", 123, "qeqwewq");

        persister.delete(person);

        assertThatThrownBy(() -> jdbcTemplate.queryForObject("SELECT * FROM users WHERE id=2L", new EntityRowMapper<>(Person3.class)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Expected 1 result, got 0");
    }
}
