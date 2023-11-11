package persistence.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static persistence.TestUtils.createDataDefinitionLanguageAssembler;
import static persistence.TestUtils.createDataManipulationLanguageAssembler;

import database.DatabaseServer;
import database.H2;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.EntityPersister;
import persistence.sql.ddl.assembler.DataDefinitionLanguageAssembler;
import persistence.sql.dml.assembler.DataManipulationLanguageAssembler;
import persistence.sql.usecase.GetFieldFromClassUseCase;
import persistence.sql.usecase.GetFieldValueUseCase;
import persistence.sql.usecase.GetIdDatabaseFieldUseCase;
import persistence.sql.usecase.SetFieldValueUseCase;

class EntityManagerTest {
    private final DataManipulationLanguageAssembler dataManipulationLanguageAssembler = createDataManipulationLanguageAssembler();
    private final DataDefinitionLanguageAssembler dataDefinitionLanguageAssembler = createDataDefinitionLanguageAssembler();
    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private EntityPersister entityPersister;
    private EntityLoader entityLoader;
    private EntityManager entityManager;


    @BeforeEach
    void setup() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        entityPersister = new EntityPersister(dataManipulationLanguageAssembler, jdbcTemplate);
        entityLoader = new EntityLoader(new GetFieldFromClassUseCase(), new SetFieldValueUseCase(), jdbcTemplate, dataManipulationLanguageAssembler);
        entityManager = new EntityManagerImpl(entityLoader,
            entityPersister, new GetIdDatabaseFieldUseCase(new GetFieldFromClassUseCase()), new GetFieldValueUseCase(), new SetFieldValueUseCase());
        jdbcTemplate.execute(dataDefinitionLanguageAssembler.assembleCreateTableQuery(Person.class));
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(dataDefinitionLanguageAssembler.assembleDropTableQuery(Person.class));
        server.stop();
    }

    @Test
    void find() {
        Person person = new Person("tongnamuu", 11, "tongnamuu@naver.com");
        jdbcTemplate.execute(dataManipulationLanguageAssembler.generateInsert(person));

        Person findPerson = entityManager.find(Person.class, 1L);
        assertAll(
            () -> assertThat(findPerson.getId()).isEqualTo(1L),
            () -> assertThat(findPerson.getName()).isEqualTo(person.getName()),
            () -> assertThat(findPerson.getAge()).isEqualTo(person.getAge()),
            () -> assertThat(findPerson.getEmail()).isEqualTo(person.getEmail())
        );
    }

    @Test
    void persist() {
        // when, then
        Person person = (Person) entityManager.persist(new Person("tongnamuu", 11, "tongnamuu@naver.com"));
        assertAll(
            () -> assertThat(person.getId()).isEqualTo(1L),
            () -> assertThat(person.getName()).isEqualTo(person.getName()),
            () -> assertThat(person.getAge()).isEqualTo(person.getAge()),
            () -> assertThat(person.getEmail()).isEqualTo(person.getEmail())
        );
    }

    @Test
    void remove() {
        // given
        Person person = new Person("tongnamuu", 11, "tongnamuu@naver.com");
        jdbcTemplate.execute(dataManipulationLanguageAssembler.generateInsert(person));
        Person findPerson = entityManager.find(Person.class, 1L);

        // when
        entityManager.remove(findPerson);
        Person findPerson2 = entityManager.find(Person.class, 1L);

        // then
        assertThat(findPerson).isNotNull();
        assertThat(findPerson2).isNull();
    }
}
