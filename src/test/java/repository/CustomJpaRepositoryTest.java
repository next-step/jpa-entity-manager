package repository;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityManager;
import persistence.entity.EntityManagerImpl;
import persistence.entity.Person;
import persistence.entity.context.PersistenceContext;
import persistence.entity.context.PersistenceContextImpl;
import persistence.entity.entry.EntityEntry;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.EntityPersister;
import persistence.sql.ddl.assembler.DataDefinitionLanguageAssembler;
import persistence.sql.dml.assembler.DataManipulationLanguageAssembler;
import persistence.sql.usecase.GetFieldFromClass;
import persistence.sql.usecase.SetFieldValue;

class CustomJpaRepositoryTest {
    private final DataManipulationLanguageAssembler dataManipulationLanguageAssembler = createDataManipulationLanguageAssembler();
    private final DataDefinitionLanguageAssembler dataDefinitionLanguageAssembler = createDataDefinitionLanguageAssembler();
    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private PersistenceContext persistenceContext;
    private EntityPersister entityPersister;
    private EntityLoader entityLoader;
    private EntityEntry entityEntry;
    private EntityManager entityManager;

    private CustomJpaRepository<Person, Long> customJpaRepository;

    @BeforeEach
    void setup() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        entityPersister = new EntityPersister(dataManipulationLanguageAssembler, jdbcTemplate);
        entityLoader = new EntityLoader(new GetFieldFromClass(), new SetFieldValue(), jdbcTemplate, dataManipulationLanguageAssembler);
        persistenceContext = new PersistenceContextImpl();
        entityEntry = new EntityEntry();
        entityManager = new EntityManagerImpl(
            entityEntry,
            entityPersister,
            entityLoader,
            persistenceContext
        );
        jdbcTemplate.execute(dataDefinitionLanguageAssembler.assembleCreateTableQuery(Person.class));
        customJpaRepository = new CustomJpaRepository<>(entityManager);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(dataDefinitionLanguageAssembler.assembleDropTableQuery(Person.class));
        server.stop();
    }

    @DisplayName("save 후 나오는 person 은 기존 객체와 같은 값을 갖는다.")
    @Test
    void test_insert() {
        Person person = new Person("tongnamuu", 12, "tongnamuu@naver.com");
        Person person1 = customJpaRepository.save(person);
        assertAll(
            () -> assertThat(person1.getName()).isEqualTo("tongnamuu"),
            () -> assertThat(person1.getAge()).isEqualTo(12),
            () -> assertThat(person1.getEmail()).isEqualTo("tongnamuu@naver.com")
        );
    }

    @DisplayName("update 후에는 update 된 값이 조회된다.")
    @Test
    void test_update() {
        Person person = new Person("tongnamuu", 12, "tongnamuu@naver.com");
        customJpaRepository.save(person);
        person.updateEmail("hello@gmail.com");
        Person person1 = customJpaRepository.save(person);
        entityManager.clear();
        Person findPerson = entityManager.find(person1.getClass(), person1.getId());

        assertAll(
            () -> assertThat(findPerson.getName()).isEqualTo("tongnamuu"),
            () -> assertThat(findPerson.getAge()).isEqualTo(12),
            () -> assertThat(findPerson.getEmail()).isEqualTo("hello@gmail.com")
        );
    }
}
