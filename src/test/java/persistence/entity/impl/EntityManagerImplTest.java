package persistence.entity.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.sql.SQLException;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import persistence.entity.EntityManager;
import persistence.sql.ddl.entity.Person;

class EntityManagerImplTest extends AbstractJdbcTemplateTest {
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() throws SQLException {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();

        entityManager = new EntityManagerImpl(jdbcTemplate);
    }

    @AfterEach
    void tearDown() throws SQLException {
        dropTable();

        createTable();
    }

    @DisplayName("요구사항1 - find 메서드를 통해 id에 해당하는 Person 레코드를 조회할 수 있다.")
    @ParameterizedTest(name = "id: {0}")
    @ValueSource(longs = {1, 2, 3, 4})
    void find(Long id) throws SQLException {
        // given
        initializeTable();
        Person givenPerson = idToPersonMap.get(id);

        // when
        Person entity = entityManager.find(Person.class, id);

        // then
        assertAll(
            () -> assertThat(entity).isNotNull(),
            () -> assertThat(entity).isEqualTo(givenPerson)
        );
    }

    @DisplayName("요구사항2 - persist (insert) 메서드를 통해 Entity를 저장할 수 있다.")
    @Test
    void persist() {
        // given
        Person person = new Person("test", 20, "test@gmail.com");

        // when
        Object savedEntity = entityManager.persist(person);

        // then
        assertThat(savedEntity).isEqualTo(person);
    }

    @DisplayName("요구사항3 - remove (delete) 메서드를 통해 특정 Entity를 삭제할 수 있다.")
    @ParameterizedTest(name = "person: {0}")
    @MethodSource("providePerson")
    void remove(Person givenPerson) throws SQLException {
        // given
        initializeTable();
        Long id = givenPerson.getId();

        // when
        entityManager.remove(givenPerson);

        // then
        assertThatExceptionOfType(RuntimeException.class)
            .isThrownBy(() ->
                entityManager.find(Person.class, id)
            );

        Integer totalCountOfEntity = selectCountOfTable();
        assertThat(totalCountOfEntity).isEqualTo(idToPersonMap.size() - 1);
    }
}
