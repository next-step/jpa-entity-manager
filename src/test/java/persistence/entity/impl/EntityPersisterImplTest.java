package persistence.entity.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import persistence.entity.EntityManager;
import persistence.entity.EntityPersister;
import persistence.sql.ddl.entity.Person;

class EntityPersisterImplTest extends AbstractJdbcTemplateTest {
    private EntityPersister entityPersister;

    private EntityManager entityManager;

    @BeforeEach
    public void setUp() throws SQLException {
        JdbcTemplate jdbcTemplate = getJdbcTemplate();

        entityManager = new EntityManagerImpl(jdbcTemplate);

        entityPersister = new EntityPersisterImpl(jdbcTemplate);

        dropTable();

        createTable();

        initializeTable();
    }

    @DisplayName("요구사항 1 - 엔터티의 데이터베이스 매핑, 쿼리 생성 및 실행 (update)")
    @ParameterizedTest
    @MethodSource("providePerson")
    void update(Person givenPerson) {
        // given
        Person needUpdatePerson = new Person(givenPerson.getId(), "user3", 60, "test2@gmail.com");

        // when
        boolean isUpdated = entityPersister.update(needUpdatePerson);

        // then
        assertTrue(isUpdated);

        Person findPerson = entityManager.find(Person.class, givenPerson.getId());
        assertThat(findPerson).isEqualTo(needUpdatePerson);
    }

    @DisplayName("요구사항 1 - 엔터티의 데이터베이스 매핑, 쿼리 생성 및 실행 (insert)")
    @Test
    void insert() {
        // given
        Person givenPerson = new Person(getCurrentId(), "user9999", 60, "test1234@gmail.com");

        // when
        entityPersister.insert(givenPerson);

        // then
        Person findPerson = entityManager.find(Person.class, givenPerson.getId());

        assertThat(findPerson).isEqualTo(givenPerson);
    }

    @DisplayName("요구사항 1 - 엔터티의 데이터베이스 매핑, 쿼리 생성 및 실행 (delete)")
    @ParameterizedTest
    @MethodSource("providePerson")
    void delete(Person givenPerson) {
        // when
        Long givenId = givenPerson.getId();
        entityPersister.delete(givenPerson);

        // then
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
            entityManager.find(Person.class, givenId)
        );

        List<Long> idList = entityManager.findAll(Person.class).stream().map(Person::getId)
            .collect(Collectors.toList());

        assertThat(idList).doesNotContain(givenPerson.getId());
    }
}
