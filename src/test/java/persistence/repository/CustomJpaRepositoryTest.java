package persistence.repository;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.context.SimplePersistenceContext;
import persistence.entity.EntityManager;
import persistence.entity.SimpleEntityManager;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dialect.Dialect;
import persistence.sql.dialect.H2Dialect;

import java.sql.SQLException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CustomJpaRepositoryTest {
    DatabaseServer databaseServer;
    private JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;
    private SimplePersistenceContext persistenceContext;
    private CustomJpaRepository<Person, Long> customJpaRepository;
    private final Dialect DIALECT = new H2Dialect();

    @BeforeEach
    void setUp() throws SQLException {
        databaseServer = new H2();
        databaseServer.start();

        jdbcTemplate = new JdbcTemplate(databaseServer.getConnection());
        persistenceContext = new SimplePersistenceContext();
        jdbcTemplate.execute(CreateQueryBuilder.builder()
                .dialect(DIALECT)
                .entity(Person.class)
                .build()
                .generateQuery());

        entityManager = new SimpleEntityManager(jdbcTemplate, DIALECT, persistenceContext);
        customJpaRepository = new CustomJpaRepository<>(entityManager);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(DropQueryBuilder.builder()
                .dialect(DIALECT)
                .entity(Person.class)
                .build()
                .generateQuery());

        databaseServer.stop();
    }

    @Test
    @DisplayName("기존 객체가 없을 경우 save 테스트")
    void save() {
        // given
        final Person person = Person.of("test", 11, "test12@gmail.com");

        // when
        Person savedPerson = customJpaRepository.save(person);

        // then
        assertAll(
                () -> assertThat(savedPerson.getId()).isEqualTo(1L),
                () -> assertThat(savedPerson.getName()).isEqualTo(person.getName()),
                () -> assertThat(savedPerson.getAge()).isEqualTo(person.getAge()),
                () -> assertThat(savedPerson.getEmail()).isEqualTo(person.getEmail())
        );
    }

    @Test
    @DisplayName("기존 객체가 있을 경우 Dirty Checking 테스트 - merge")
    void merge() {
        // given
        final Person person = Person.of("test", 11, "test12@gmail.com");
        Person savedPerson = customJpaRepository.save(person);
        final String newName = "newName";
        savedPerson.setName(newName);

        // when
        Person resultPerson = customJpaRepository.save(savedPerson);

        // then
        assertAll(
                () -> assertThat(resultPerson.getId()).isEqualTo(savedPerson.getId()),
                () -> assertThat(resultPerson.getName()).isEqualTo(newName),
                () -> assertThat(resultPerson.getAge()).isEqualTo(savedPerson.getAge()),
                () -> assertThat(resultPerson.getEmail()).isEqualTo(savedPerson.getEmail())
        );
    }
}