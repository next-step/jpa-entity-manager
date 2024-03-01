package persistence.entity;

import database.DatabaseServer;
import database.H2;
import domain.Person;
import java.sql.SQLException;
import jdbc.JdbcTemplate;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.fixture.PersonFixture;
import persistence.sql.ddl.DdlGenerator;
import persistence.sql.dialect.h2.H2Dialect;

@DisplayName("SimpleEntityManager class 의")
class SimpleEntityManagerTest {

    private DatabaseServer server;

    private JdbcTemplate jdbcTemplate;
    private DdlGenerator ddlGenerator;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() throws SQLException {
        server = new H2();
        server.start();

        jdbcTemplate = new JdbcTemplate(server.getConnection());
        ddlGenerator = DdlGenerator.getInstance(H2Dialect.getInstance());
        entityManager = SimpleEntityManager.from(jdbcTemplate);
        jdbcTemplate.execute(ddlGenerator.generateCreateQuery(Person.class));
    }

    @AfterEach
    void tearDown() {
        jdbcTemplate.execute(ddlGenerator.generateDropQuery(Person.class));
        server.stop();
    }

    @DisplayName("persist 메서드는")
    @Nested
    class Persist {

        @DisplayName("Person entity를 저장 한다.")
        @Test
        void persistTest_whenInsert() {
            //given
            Person person = PersonFixture.createPerson();

            //when
            entityManager.persist(person);

            //then
            Person foundPerson = entityManager.find(person.getClass(), 1L);
            assertAll(
                () -> assertEquals(person.getName(), foundPerson.getName()),
                () -> assertEquals(person.getAge(), foundPerson.getAge()),
                () -> assertEquals(person.getEmail(), foundPerson.getEmail())
            );
        }
    }

    @DisplayName("find 메서드는")
    @Nested
    class Find {

        @DisplayName("Person entity를 검색 할 수 있다.")
        @Test
        void findTest() {
            // given
            Person person = PersonFixture.createPerson();
            entityManager.persist(person);

            // when
            Person foundPerson = entityManager.find(Person.class, 1L);

            // then
            assertAll(
                () -> assertEquals(person.getName(), foundPerson.getName()),
                () -> assertEquals(person.getAge(), foundPerson.getAge()),
                () -> assertEquals(person.getEmail(), foundPerson.getEmail())
            );
        }

        @DisplayName("같은 Person entity를 두 번 검색하면 캐싱된 entity를 반환한다.")
        @Test
        void findTest_whenFindTwice() {
            // given
            Person person = PersonFixture.createPerson();
            entityManager.persist(person);

            // when
            Person foundPerson1 = entityManager.find(Person.class, 1L);
            Person foundPerson2 = entityManager.find(Person.class, 1L);

            // then
            assertEquals(foundPerson1, foundPerson2);
        }
    }

    @DisplayName("remove 메서드는")
    @Nested
    class Remove {

        @DisplayName("특정 Person을 삭제 할 수 있다.")
        @Test
        void deleteTest() {
            //given
            Person person = PersonFixture.createPerson();
            entityManager.persist(person);
            Person person1 = entityManager.find(Person.class, 1L);

            //when
            entityManager.remove(person1);

            //then
            assertThatThrownBy(() -> entityManager.find(Person.class, 1L))
                .isInstanceOf(RuntimeException.class);
        }
    }

    @DisplayName("merge 메서드는")
    @Nested
    class Merge {

        @DisplayName("Person entity를 수정 할 수 있다.")
        @Test
        void mergeTest() {
            //given
            Person person = PersonFixture.createPerson();
            entityManager.persist(person);
            person = entityManager.find(Person.class, 1L);
            person.updateName("user2");

            //when
            entityManager.merge(person);

            //then
            person = entityManager.find(Person.class, 1L);
            assertEquals(person.getName(), "user2");
        }

        @DisplayName("Person entity를 수정하지 않으면 수정하지 않는다.")
        @Test
        void mergeTest_whenNotUpdate() {
            //given
            Person person = PersonFixture.createPerson();
            entityManager.persist(person);
            person = entityManager.find(Person.class, 1L);
            Person person1 = entityManager.find(Person.class, 1L);

            //when
            entityManager.merge(person);

            //then
            assertEquals(person, person1);
        }
    }
}
