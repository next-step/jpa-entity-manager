package persistence.sql.dml;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import java.sql.SQLException;
import java.util.List;
import jdbc.EntityRowMapper;
import jdbc.JdbcTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import persistence.sql.ddl.DdlGenerator;
import persistence.sql.dialect.h2.H2Dialect;

@DisplayName("DmlGenerator 통합 테스트")
class DmlGeneratorIntegrationTest {

    private DatabaseServer server;

    private JdbcTemplate jdbcTemplate;
    private DdlGenerator ddlGenerator;
    private DmlGenerator dmlGenerator;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        ddlGenerator = DdlGenerator.getInstance(H2Dialect.getInstance());
        dmlGenerator = DmlGenerator.getInstance();

        jdbcTemplate.execute(ddlGenerator.generateCreateQuery(Person.class));
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(ddlGenerator.generateDropQuery(Person.class));
        server.stop();
    }

    @DisplayName("generateInsertQuery는 Insert 쿼리가 정상적으로 호출되는지 확인한다.")
    @CsvSource({
        "user1, 1, test1@abc.com, 1",
        "user2, 2, test2@abc.com, 2",
        "user3, 3, test3@abc.com, 3",
        "user4, 4, test4@abc.com, 4"
    })
    @ParameterizedTest
    void insertQueryTest(String name, int age, String email, int index) {
        Person person = Person.of(name, age, email, index);
        jdbcTemplate.execute(dmlGenerator.generateInsertQuery(person));
    }

    @DisplayName("generateSelectQuery Select 쿼리가 정상적으로 호출되는지 확인한다.")
    @Test
    void selectQueryTest() {
        insertQueryTest("user1", 1, "abc@test.co", 1);
        insertQueryTest("user2", 1, "abc@test.co", 1);
        insertQueryTest("user3", 1, "abc@test.co", 1);
        insertQueryTest("user4", 1, "abc@test.co", 1);

        List<Person> people = jdbcTemplate.query(dmlGenerator.generateSelectQuery(Person.class), resultSet ->
            new EntityRowMapper<>(Person.class).mapRow(resultSet));

        assertAll(
            () -> assertThat(people).isNotEmpty(),
            () -> assertThat(people).hasSize(4),
            () -> assertThat(people.get(0).getName()).isEqualTo("user1"),
            () -> assertThat(people.get(1).getName()).isEqualTo("user2"),
            () -> assertThat(people.get(2).getName()).isEqualTo("user3"),
            () -> assertThat(people.get(3).getName()).isEqualTo("user4")
        );
    }

    @DisplayName("generateSelectQuery id 조건 select 쿼리가 정상적으로 호출되는지 확인한다.")
    @CsvSource({
        "user1, 1, test1@abc.com, 1, 1",
        "user2, 2, test2@abc.com, 2, 1",
        "user3, 3, test3@abc.com, 3, 1",
        "user4, 4, test4@abc.com, 4, 1"
    })
    @ParameterizedTest
    void selectQueryWithIdTest(String name, int age, String email, int index, long id) {
        insertQueryTest(name, age, email, index);

        Person person = jdbcTemplate.queryForObject(dmlGenerator.generateSelectQuery(Person.class, id), resultSet ->
            new EntityRowMapper<>(Person.class).mapRow(resultSet));

        assertAll(
            () -> assertThat(person).isNotNull(),
            () -> assertThat(person.getName()).isEqualTo(name),
            () -> assertThat(person.getAge()).isEqualTo(age),
            () -> assertThat(person.getEmail()).isEqualTo(email)
        );
    }

    @DisplayName("generateDeleteQuery Delete 쿼리가 정상적으로 호출되는지 확인한다.")
    @Test
    void deleteQueryTest() {
        insertQueryTest("user1", 1, "abc@test.co", 1);
        insertQueryTest("user2", 1, "abc@test.co", 1);
        insertQueryTest("user3", 1, "abc@test.co", 1);
        insertQueryTest("user4", 1, "abc@test.co", 1);

        jdbcTemplate.execute(dmlGenerator.generateDeleteQuery(Person.of(1L, "user1", 1, "abc@test.co")));
        jdbcTemplate.execute(dmlGenerator.generateDeleteQuery(Person.of(2L, "user2", 1, "abc@test.co")));
        jdbcTemplate.execute(dmlGenerator.generateDeleteQuery(Person.of(3L, "user3", 1, "abc@test.co")));
        jdbcTemplate.execute(dmlGenerator.generateDeleteQuery(Person.of(4L, "user4", 1, "abc@test.co")));

        List<Person> personList = jdbcTemplate.query(dmlGenerator.generateSelectQuery(Person.class), resultSet ->
            new EntityRowMapper<>(Person.class).mapRow(resultSet));

        assertAll(
            () -> assertThat(personList).isEmpty()
        );
    }

    @DisplayName("generateDeleteQuery id 조건 Delete 쿼리가 정상적으로 호출되는지 확인한다.")
    @Test
    void deleteQueryWithIdTest() {
        insertQueryTest("user1", 1, "abc@test.co", 1);
        insertQueryTest("user2", 1, "abc@test.co", 1);
        insertQueryTest("user3", 1, "abc@test.co", 1);
        insertQueryTest("user4", 1, "abc@test.co", 1);

        jdbcTemplate.execute(dmlGenerator.generateDeleteQuery(Person.of(4L, "user4", 1, "abc@test.co")));

        List<Person> personList = jdbcTemplate.query(dmlGenerator.generateSelectQuery(Person.class), resultSet ->
            new EntityRowMapper<>(Person.class).mapRow(resultSet));

        assertAll(
            () -> assertThat(personList).isNotEmpty(),
            () -> assertThat(personList).hasSize(3)
        );
    }

    @DisplayName("generateUpdateQuery Update 쿼리가 정상적으로 호출되는지 확인한다.")
    @Test
    void updateQueryTest() {
        insertQueryTest("user1", 1, "abc@test.co", 1);

        jdbcTemplate.execute(dmlGenerator.generateUpdateQuery(Person.of(1L, "user2", 2, "abcd@test.co")));

        Person person = jdbcTemplate.queryForObject(dmlGenerator.generateSelectQuery(Person.class, 1L), resultSet ->
            new EntityRowMapper<>(Person.class).mapRow(resultSet));

        assertAll(
            () -> assertThat(person.getName()).isEqualTo("user2"),
            () -> assertThat(person.getAge()).isEqualTo(2),
            () -> assertThat(person.getEmail()).isEqualTo("abcd@test.co")
        );
    }
}
