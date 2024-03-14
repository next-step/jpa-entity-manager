package persistence.entity.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import java.util.List;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import persistence.entity.EntityLoader;
import persistence.sql.ddl.entity.Person;

class EntityLoaderImplTest extends AbstractJdbcTemplateTest {
    private EntityLoader entityLoader;

    @BeforeEach
    public void setUp() throws SQLException {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();

        entityLoader = new EntityLoaderImpl(jdbcTemplate);

        dropTable();

        createTable();

        initializeTable();
    }

    @DisplayName("요구사항 2 - EntityManager 의 책임 줄여주기 (find)")
    @ParameterizedTest
    @MethodSource("providePerson")
    void select(Person givenPerson) {
        // given
        Long givenId = givenPerson.getId();

        // when
        Person person = entityLoader.select(Person.class, givenId);

        // then
        assertThat(person).isEqualTo(givenPerson);
    }

    @DisplayName("요구사항 2 - EntityManager 의 책임 줄여주기 (findAll)")
    @Test
    void selectAll() {
        // when
        List<Person> persons = entityLoader.selectAll(Person.class);

        // then
        assertThat(persons).containsExactlyInAnyOrderElementsOf(idToPersonMap.values());
    }
}
