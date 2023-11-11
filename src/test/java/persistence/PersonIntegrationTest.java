package persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static persistence.TestUtils.createDataDefinitionLanguageAssembler;
import static persistence.TestUtils.createDataManipulationLanguageAssembler;

import database.DatabaseServer;
import database.H2;
import java.sql.SQLException;
import java.util.List;
import jdbc.JdbcTemplate;
import jdbc.PersonRowMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;
import persistence.sql.ddl.assembler.DataDefinitionLanguageAssembler;
import persistence.sql.dml.assembler.DataManipulationLanguageAssembler;

class PersonIntegrationTest {
    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private final DataDefinitionLanguageAssembler dataDefinitionLanguageAssembler = createDataDefinitionLanguageAssembler();
    private final DataManipulationLanguageAssembler dataManipulationLanguageAssembler = createDataManipulationLanguageAssembler();


    @BeforeEach
    void setup() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(dataDefinitionLanguageAssembler.assembleCreateTableQuery(Person.class));
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(dataDefinitionLanguageAssembler.assembleDropTableQuery(Person.class));
        server.stop();
    }

    @DisplayName("insert 하면 전체 조회시 1개의 결과가 나온다")
    @Test
    void testFindAll() {
        // given
        final String name = "tongnamuu";
        final int age = 14;
        final String email = "tongnamuu@naver.com";
        PersonRowMapper personRowMapper = new PersonRowMapper();
        String insertQuery = dataManipulationLanguageAssembler.generateInsert(new Person(name, age, email));
        String selectQuery = dataManipulationLanguageAssembler.generateSelect(Person.class);

        // when
        jdbcTemplate.execute(insertQuery);
        List<Person> persons = jdbcTemplate.query(selectQuery, personRowMapper);
        assertAll(
            () -> assertThat(persons.size()).isEqualTo(1),
            () -> assertThat(persons.get(0).getName()).isEqualTo(name),
            () -> assertThat(persons.get(0).getAge()).isEqualTo(age),
            () -> assertThat(persons.get(0).getEmail()).isEqualTo(email)
        );
    }

    @DisplayName("insert 하면 findById 시 1개의 결과가 나온다")
    @Test
    void testFindById() {
        // given
        final String name = "tongnamuu";
        final int age = 14;
        final String email = "tongnamuu@naver.com";
        PersonRowMapper personRowMapper = new PersonRowMapper();
        String insertQuery = dataManipulationLanguageAssembler.generateInsert(new Person(name, age, email));
        String selectQuery = dataManipulationLanguageAssembler.generateSelectWithWhere(Person.class, 1L);

        // when
        jdbcTemplate.execute(insertQuery);
        List<Person> persons = jdbcTemplate.query(selectQuery, personRowMapper);
        assertAll(
            () -> assertThat(persons.size()).isEqualTo(1),
            () -> assertThat(persons.get(0).getName()).isEqualTo(name),
            () -> assertThat(persons.get(0).getAge()).isEqualTo(age),
            () -> assertThat(persons.get(0).getEmail()).isEqualTo(email)
        );
    }

    @DisplayName("존재하던 delete 하면 findAll 시 0개의 결과가 나온다")
    @Test
    void testDelete() {
        // given
        final String name = "tongnamuu";
        final int age = 14;
        final String email = "tongnamuu@naver.com";
        Person p = new Person(name, age, email);
        PersonRowMapper personRowMapper = new PersonRowMapper();
        String insertQuery = dataManipulationLanguageAssembler.generateInsert(p);
        Long id = jdbcTemplate.insertSingle(insertQuery);
        p.setId(id);
        String deleteQuery = dataManipulationLanguageAssembler.generateDeleteWithWhere(p);

        // when
        jdbcTemplate.execute(deleteQuery);
        List<Person> persons = jdbcTemplate.query(dataManipulationLanguageAssembler.generateSelect(Person.class), personRowMapper);
        assertThat(persons.size()).isZero();
    }

    @DisplayName("업데이트 쿼리를 날린 후 조회하면 업데이트 된 객체가 반환된다")
    @Test
    void testUpdate() {
        // given
        Person p = new Person("tongnamuu", 14, "tongnamuu@naver.com");
        PersonRowMapper personRowMapper = new PersonRowMapper();
        String insertQuery = dataManipulationLanguageAssembler.generateInsert(p);
        Long id = jdbcTemplate.insertSingle(insertQuery);
        Person beforeUpdatePerson = jdbcTemplate.queryForObject(
            dataManipulationLanguageAssembler.generateSelectWithWhere(Person.class, id),
            personRowMapper
        );

        // when
        Person updatePerson = new Person("tongnamuu2", 15, "tongnamuu@gmail.com");
        updatePerson.setId(id);
        String updateQuery = dataManipulationLanguageAssembler.generateUpdate(updatePerson);
        jdbcTemplate.execute(updateQuery);

        Person afterUpdatePerson = jdbcTemplate.queryForObject(
            dataManipulationLanguageAssembler.generateSelectWithWhere(Person.class, id),
            personRowMapper
        );

        // then
        assertAll(
            () -> assertThat(afterUpdatePerson.getName()).isEqualTo("tongnamuu2"),
            () -> assertThat(afterUpdatePerson.getAge()).isEqualTo(15),
            () -> assertThat(afterUpdatePerson.getEmail()).isEqualTo("tongnamuu@gmail.com"),
            () -> assertThat(beforeUpdatePerson.getName()).isEqualTo("tongnamuu"),
            () -> assertThat(beforeUpdatePerson.getAge()).isEqualTo(14),
            () -> assertThat(beforeUpdatePerson.getEmail()).isEqualTo("tongnamuu@naver.com")
        );
    }
}
