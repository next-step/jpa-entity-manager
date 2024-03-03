package persistence.entity;

import database.sql.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.context.ObjectNotFoundException;
import testsupport.H2DatabaseTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static testsupport.EntityTestUtils.assertSamePerson;

class EntityManagerImplScenarioTest extends H2DatabaseTest {
    private EntityManagerImpl entityManager;

    @BeforeEach
    void setUp() {
        entityManager = EntityManagerImpl.from(loggingJdbcTemplate);
    }

    @Test
    @DisplayName("저장하고, 읽고, 지우고 다시 찾는 시나리오")
    void scenario1() {
        Person person = new Person("abc", 7, "def@example.com");
        entityManager.persist(person);

        Person fetchedPerson = entityManager.find(Person.class, 1L);
        entityManager.find(Person.class, 1L);
        entityManager.find(Person.class, 1L);
        entityManager.find(Person.class, 1L);

        entityManager.remove(fetchedPerson);

        assertAll(
                () -> assertSamePerson(fetchedPerson, person, false),
                () -> assertThrows(ObjectNotFoundException.class, () -> entityManager.find(Person.class, 1L)),
                () -> assertThat(loggingJdbcTemplate.executedQueries).containsExactly(
                        "INSERT INTO users (nick_name, old, email) VALUES ('abc', 7, 'def@example.com')",
                        "SELECT id, nick_name, old, email FROM users WHERE id = 1",
                        "DELETE FROM users WHERE id = 1"
                )
        );
    }

    @Test
    void scenario2() {
        Person person = new Person("abc", 7, "def@example.com");
        entityManager.persist(person);

        Person fetchedPerson = entityManager.find(Person.class, 1L);
        entityManager.remove(fetchedPerson);

        assertThrows(ObjectNotFoundException.class, () ->
                entityManager.persist(new Person(fetchedPerson.getId(), "newname", 8, "newemail@test.com"))
        );
    }

    @Test
    @DisplayName("1st 캐시에 없는 row 를 id 로 업데이트하려고 해도 insert 됨. (IDENTITY 전략) load 후에 persist 하면 update 됨")
    void scenario3() {
        loggingJdbcTemplate.execute("INSERT INTO users (id, nick_name, old, email) VALUES (20, '가나다', 21, 'email@test.com')");

        // id 20 무시됨
        entityManager.persist(new Person(20L, "가나다라", 22, "email2@test.com"));

        entityManager.find(Person.class, 20L);
        entityManager.find(Person.class, 20L);
        entityManager.persist(new Person(20L, "가나다라마", 22, "email2@test.com"));

        assertThat(loggingJdbcTemplate.executedQueries).containsExactly(
                "INSERT INTO users (id, nick_name, old, email) VALUES (20, '가나다', 21, 'email@test.com')",
                "INSERT INTO users (nick_name, old, email) VALUES ('가나다라', 22, 'email2@test.com')",
                "SELECT id, nick_name, old, email FROM users WHERE id = 1",
                "SELECT id, nick_name, old, email FROM users WHERE id = 20",
                "UPDATE users SET nick_name = '가나다라마', old = 22, email = 'email2@test.com' WHERE id = 20"
        );
    }

    @Test
    @DisplayName("지운 걸 또 지워보기")
    void scenario4() {
        Person person = new Person("가나다라", 22, "email2@test.com");
        entityManager.persist(person);

        Person fetchedPerson = entityManager.find(Person.class, 1L);

        entityManager.remove(fetchedPerson);
        entityManager.remove(fetchedPerson);
        System.out.println(loggingJdbcTemplate.executedQueries);

        assertThat(loggingJdbcTemplate.executedQueries).containsExactly(
                "INSERT INTO users (nick_name, old, email) VALUES ('가나다라', 22, 'email2@test.com')",
                "SELECT id, nick_name, old, email FROM users WHERE id = 1",
                "DELETE FROM users WHERE id = 1"
        );
    }

    @Test
    @DisplayName("아직 db 에 없는 ID 를 가진 객체를 persist 해본다.")
    void scenario5() {
        assertThat(entityManager.find(Person.class, 20L)).isNull();

        Person person = new Person(20L, "가나다라", 22, "email2@test.com");
        entityManager.persist(person);

        Person fetched = entityManager.find(Person.class, 1L);
        assertAll(
                () -> assertSamePerson(fetched, person, false),
                () -> assertThat(loggingJdbcTemplate.executedQueries).containsExactly(
                        "SELECT id, nick_name, old, email FROM users WHERE id = 20",
                        "INSERT INTO users (nick_name, old, email) VALUES ('가나다라', 22, 'email2@test.com')",
                        "SELECT id, nick_name, old, email FROM users WHERE id = 1")
        );
    }

}
