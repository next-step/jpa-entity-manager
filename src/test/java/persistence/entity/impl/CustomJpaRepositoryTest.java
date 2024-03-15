package persistence.entity.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import persistence.entity.EntityManager;
import persistence.sql.ddl.entity.Person;

class CustomJpaRepositoryTest extends AbstractJdbcTemplateTest {
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() throws SQLException {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();

        entityManager = new EntityManagerImpl(jdbcTemplate);

        dropTable();

        createTable();

        initializeTable();
    }

    @DisplayName("요구사항3 - 더티체킹 구현")
    @ParameterizedTest
    @MethodSource("providePersonId")
    void test(Long givenPersonId) {
        // given
        Person givenPerson = entityManager.find(Person.class, givenPersonId);

        givenPerson.changeEmail("changed@gmail.com");

        Person changedPerson = clonePerson(givenPerson);

        // when
        entityManager.persist(givenPerson);

        // then
        Person findPerson = entityManager.find(Person.class, givenPersonId);
        assertThat(findPerson).isEqualTo(changedPerson);
    }

    private Person clonePerson(Person person) {
        return new Person(person.getId(), person.getName(), person.getAge(), person.getEmail());
    }
}
