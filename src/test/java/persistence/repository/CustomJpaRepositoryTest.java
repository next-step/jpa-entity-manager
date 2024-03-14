package persistence.repository;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    private Dialect DIALECT = new H2Dialect();

    @BeforeEach
    void setUp() throws SQLException {
        databaseServer = new H2();
        databaseServer.start();

        jdbcTemplate = new JdbcTemplate(databaseServer.getConnection());
        jdbcTemplate.execute(CreateQueryBuilder.builder()
                .dialect(DIALECT)
                .entity(Person.class)
                .build()
                .generateQuery());

        entityManager = new SimpleEntityManager(jdbcTemplate, DIALECT);
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
    @DisplayName("JpaRepository save 테스트")
    void save() {
        // given
        final CustomJpaRepository<Person, Long> customJpaRepository = new CustomJpaRepository<>(entityManager);
        final Person person = Person.of("test", 11, "test12@gmail.com");

        // when
        Person savedPerson = customJpaRepository.save(person);

        // then
        assertThat(savedPerson).isEqualTo(person);
    }

    @Test
    @DisplayName("JpaRepository merge 테스트")
    void merge() {
        // given
        final CustomJpaRepository<Person, Long> customJpaRepository = new CustomJpaRepository<>(entityManager);
        final Person person = Person.of("test", 11, "test12@gmail.com");
        Person savedPerson = customJpaRepository.save(person);
        final String newName = "newName";
        savedPerson.setName(newName);

        // when
        Person resultPerson = customJpaRepository.save(savedPerson);

        // then
        assertAll(
                () -> assertThat(resultPerson).isEqualTo(savedPerson),
                () -> assertThat(resultPerson.getName()).isEqualTo(newName)
        );
    }
}