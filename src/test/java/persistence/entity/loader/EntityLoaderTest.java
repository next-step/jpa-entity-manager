package persistence.entity.loader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import database.DatabaseServer;
import database.H2;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.TestUtils;
import persistence.entity.Person;
import persistence.sql.ddl.assembler.DataDefinitionLanguageAssembler;
import persistence.sql.dml.assembler.DataManipulationLanguageAssembler;
import persistence.sql.usecase.GetFieldFromClass;
import persistence.sql.usecase.SetFieldValue;

class EntityLoaderTest {
    private final DataManipulationLanguageAssembler dataManipulationLanguageAssembler = TestUtils.createDataManipulationLanguageAssembler();
    private final DataDefinitionLanguageAssembler dataDefinitionLanguageAssembler = TestUtils.createDataDefinitionLanguageAssembler();
    private EntityLoader entityLoader;
    private JdbcTemplate jdbcTemplate;
    private DatabaseServer server;

    @BeforeEach
    void setup() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(dataDefinitionLanguageAssembler.assembleCreateTableQuery(Person.class));
        entityLoader = new EntityLoader(
            new GetFieldFromClass(),
            new SetFieldValue(),
            jdbcTemplate,
            dataManipulationLanguageAssembler
        );
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(dataDefinitionLanguageAssembler.assembleDropTableQuery(Person.class));
        server.stop();
    }

    @DisplayName("존재하지 않는 객체를 조회하면 null 을 반환한다.")
    @Test
    void findNull() {
        Person person = entityLoader.find(Person.class, 1L);
        assertThat(person).isNull();
    }

    @DisplayName("존재하는 객체를 조회하면 해당 객체를 반환한다.")
    @Test
    void findExist() {
        Person person = new Person("tongnamuu", 11, "tongnamuu@gmail.com");
        Long id = jdbcTemplate.insertSingle(dataManipulationLanguageAssembler.generateInsert(person));

        Person findPerson = entityLoader.find(Person.class, id);
        assertAll(
            () -> assertThat(findPerson.getId()).isEqualTo(id),
            () -> assertThat(findPerson.getName()).isEqualTo(person.getName()),
            () -> assertThat(findPerson.getAge()).isEqualTo(person.getAge()),
            () -> assertThat(findPerson.getEmail()).isEqualTo(person.getEmail())
        );
    }
}
