package persistence.entity;

import database.Database;
import database.DatabaseServer;
import database.H2;
import database.SimpleDatabase;
import jakarta.persistence.EntityExistsException;
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
import persistence.study.sql.ddl.Person3RowMapper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class SimpleEntityManagerTest {

    private static DatabaseServer server;
    private static JdbcTemplate jdbcTemplate;

    private static DDLQueryBuilder ddlQueryBuilder;
    private static DMLQueryBuilder dmlQueryBuilder;

    private static EntityPersister entityPersister;
    private static EntityLoader entityLoader;
    private static EntityManager entityManager;

    @BeforeAll
    static void initialize() throws SQLException {
        server = new H2();
        server.start();

        Connection jdbcConnection = server.getConnection();
        jdbcTemplate = new JdbcTemplate(jdbcConnection);
        Database database = new SimpleDatabase(jdbcTemplate);

        entityPersister = new EntityPersister(database);
        entityLoader = new EntityLoader(database);
        entityManager = new SimpleEntityManager(entityPersister, entityLoader);

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

        persons.forEach(person -> entityManager.persist(person));
    }

    @AfterEach
    void setDown() {
        String dropQuery = ddlQueryBuilder.buildDropQuery();
        jdbcTemplate.execute(dropQuery);

        entityManager = new SimpleEntityManager(entityPersister, entityLoader);
    }

    private Stream<Person3> createPersons() {
        return Stream.of(
                new Person3("qwer1", 1, "email1@email.com"),
                new Person3("qwer2", 2, "email2@email.com"),
                new Person3("qwer3", 3, "email3@email.com")
        );
    }

    @DisplayName("person을 이용하여 find 메서드 테스트")
    @ParameterizedTest
    @MethodSource
    void find(EntityId id, Object person) {
        Person3 result = entityManager.find(Person3.class, id);

        assertThat(result).isEqualTo(person);
    }

    private static Stream<Arguments> find() {
        Person3 person1 = new Person3(1L, "qwer1", 1, "email1@email.com");
        Person3 person2 = new Person3(2L, "qwer2", 2, "email2@email.com");
        Person3 person3 = new Person3(3L, "qwer3", 3, "email3@email.com");

        return Stream.of(
                Arguments.arguments(new EntityId(1L), person1),
                Arguments.arguments(new EntityId(2L), person2),
                Arguments.arguments(new EntityId(3L), person3)
        );
    }

    @DisplayName("person을 이용하여 persist 메서드 테스트")
    @Test
    void persist() {
        Person3 person = new Person3(null, "qwer", 1, "email@email.com");

        entityManager.persist(person);

        Person3 findPerson = findByIdPerson(4L);
        Person3 expectPerson = new Person3(4L, "qwer", 1, "email@email.com");
        assertThat(findPerson).isEqualTo(expectPerson);
    }

    @DisplayName("이미 존재하는 엔티티인 경우 EntityExistException을 throw 한다.")
    @Test
    void persistIsExist() {
        Person3 person = new Person3(1L, "qwer", 1, "email@email.com");

        assertThatThrownBy(() -> entityManager.persist(person))
                .isInstanceOf(EntityExistsException.class);
    }

    @DisplayName("person이 없는 경우 엔티티를 create 한다.")
    @Test
    void merge() {
        Person3 person = new Person3(4L, "qwer", 123, "email@email.com");

        entityManager.merge(person);

        Person3 result = findByIdPerson(4L);
        assertThat(result).isEqualTo(person);
    }

    @DisplayName("person이 이미 있는 경우 엔티티를 update 한다.")
    @Test
    void mergeWithUpdate() {
        Person3 person = new Person3(1L, "qwerqwrwr", 1231231, "email@email,com");

        entityManager.merge(person);

        Person3 result = findByIdPerson(1L);
        assertThat(result).isEqualTo(person);
    }

    private Person3 findByIdPerson(Object id) {
        EntityId entityId = new EntityId(id);
        String findByIdQuery = dmlQueryBuilder.buildFindByIdQuery(entityId);
        return jdbcTemplate.queryForObject(findByIdQuery, new Person3RowMapper());
    }

    @DisplayName("person을 이용하여 remove 메서드 테스트")
    @Test
    void remove() {
        Person3 person = new Person3(3L, "qwer", 1, "email@email.com");

        entityManager.remove(person);

        List<Person3> findPersons = findAllPerson();
        Person3 expect1 = new Person3(1L, "qwer1", 1, "email1@email.com");
        Person3 expect2 = new Person3(2L, "qwer2", 2, "email2@email.com");
        assertSoftly(softly -> {
            softly.assertThat(findPersons).containsExactlyInAnyOrder(expect1, expect2);
            softly.assertThat(findPersons).doesNotContain(person);
        });
    }

    private List<Person3> findAllPerson() {
        String findAllQuery = dmlQueryBuilder.buildFindAllQuery();
        return jdbcTemplate.query(findAllQuery, new Person3RowMapper());
    }
}
