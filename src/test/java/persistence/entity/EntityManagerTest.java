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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.context.PersistenceContext;
import persistence.entity.context.PersistenceContextImpl;
import persistence.entity.entry.EntityEntry;
import persistence.entity.loader.EntityLoader;
import persistence.entity.persister.EntityPersister;
import persistence.sql.ddl.assembler.DataDefinitionLanguageAssembler;
import persistence.sql.dml.assembler.DataManipulationLanguageAssembler;
import persistence.sql.usecase.GetFieldFromClass;
import persistence.sql.usecase.SetFieldValue;

class EntityManagerTest {
    private final DataManipulationLanguageAssembler dataManipulationLanguageAssembler = createDataManipulationLanguageAssembler();
    private final DataDefinitionLanguageAssembler dataDefinitionLanguageAssembler = createDataDefinitionLanguageAssembler();
    private DatabaseServer server;
    private JdbcTemplate jdbcTemplate;
    private PersistenceContext persistenceContext;
    private EntityPersister entityPersister;
    private EntityLoader entityLoader;
    private EntityEntry entityEntry;
    private EntityManager entityManager;


    @BeforeEach
    void setup() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        persistenceContext = new PersistenceContextImpl();
        entityPersister = new EntityPersister(dataManipulationLanguageAssembler, jdbcTemplate);
        entityLoader = new EntityLoader(new GetFieldFromClass(), new SetFieldValue(), jdbcTemplate, dataManipulationLanguageAssembler);
        entityEntry = new EntityEntry();
        entityManager = new EntityManagerImpl(
            entityEntry,
            entityPersister,
            entityLoader,
            persistenceContext
        );
        jdbcTemplate.execute(dataDefinitionLanguageAssembler.assembleCreateTableQuery(Person.class));
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(dataDefinitionLanguageAssembler.assembleDropTableQuery(Person.class));
        server.stop();
    }

    @Test
    @DisplayName("조회 시 올바른 객체가 나온다.")
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
    @DisplayName("저장 시 올바른 객체가 나온다.")
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
    @DisplayName("삭제 시 null이 나온다.")
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

    @Test
    @DisplayName("업데이트 후 조회하면 업데이트 된 객체가 반환한다.")
    void test_update_find() {
        // given
        Person p = new Person("hello", 21, "hello@naver.com");
        entityManager.persist(p);

        // when
        p.updateEmail("test@naver.com");
        entityManager.persist(p);

        // then
        Person findPerson = entityManager.find(Person.class, p.getId());
        assertAll(
            () -> assertThat(p == findPerson).isTrue(),
            () -> assertThat(findPerson.getEmail()).isEqualTo("test@naver.com")
        );
    }

    @Test
    @DisplayName("삭제 후 조회하면 null 반환한다.")
    void test_delete_find() {
        // given
        Person p = new Person("hello", 21, "hello@naver.com");
        entityManager.persist(p);

        // when
        entityManager.remove(p);

        // then
        Person findPerson = entityManager.find(Person.class, p.getId());
        assertAll(
            () -> assertThat(findPerson).isNull()
        );
    }
}
