package persistence.entity;

import database.DatabaseServer;
import database.H2;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.CreateQueryBuilder;
import persistence.sql.ddl.DropQueryBuilder;
import persistence.sql.dml.entity.Person;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class CustomJpaRepositoryTest {

    private JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseServer server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        jdbcTemplate.execute(new CreateQueryBuilder(Person.class).build());
        entityManager = new EntityManagerImpl(jdbcTemplate);
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(new DropQueryBuilder(Person.class).build());
    }

    @Test
    @DisplayName("CustomJpaRepository 이용한 Person 저장 테스트")
    void saveTest() {
        // given
        CustomJpaRepository<Person, Long> customJpaRepository = new CustomJpaRepository<>(entityManager);
        Person person = new Person("jay", 30, "jay@mail.com");

        // when
        Person savedPerson = customJpaRepository.save(person);

        // then
        assertThat(savedPerson.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("CustomJpaRepository 이용한 Person 병합 테스트")
    void mergeTest() {
        // given
        CustomJpaRepository<Person, Long> customJpaRepository = new CustomJpaRepository<>(entityManager);
        Person person = new Person("jay", 30, "jay@mail.com");
        Person savedPerson = customJpaRepository.save(person);
        int expectedAge = 34;
        savedPerson.updateAge(expectedAge);

        // when
        Person mergedPerson = customJpaRepository.save(savedPerson);

        // then
        assertThat(mergedPerson.getAge()).isEqualTo(expectedAge);
    }

}
