package persistence.entity;

import database.sql.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import testsupport.H2DatabaseTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static testsupport.EntityTestUtils.assertSamePerson;

class PersistenceContextImplTest extends H2DatabaseTest {

    private PersistenceContextImpl persistenceContext;

    @BeforeEach
    void setUp() {
        persistenceContext = new PersistenceContextImpl(loggingJdbcTemplate);
    }

    @Test
    void scenario1() {
        Person person = new Person("abc", 7, "def@example.com");
        persistenceContext.addEntity(person);

        Person fetchedPerson = (Person) persistenceContext.getEntity(Person.class, 1L);
        persistenceContext.getEntity(Person.class, 1L);
        persistenceContext.getEntity(Person.class, 1L);
        persistenceContext.getEntity(Person.class, 1L);

        persistenceContext.removeEntity(fetchedPerson);

        assertAll(
                () -> assertSamePerson(fetchedPerson, person, false),
                () -> assertThrows(ObjectNotFoundException.class, () -> persistenceContext.getEntity(Person.class, 1L)),
                () -> assertThat(loggingJdbcTemplate.executedQueries).containsExactly(
                        "INSERT INTO users (nick_name, old, email) VALUES ('abc', 7, 'def@example.com')",
                        "SELECT max(id) as id FROM users",
                        "SELECT id, nick_name, old, email FROM users WHERE id = 1",
                        "DELETE FROM users WHERE id = 1"
                )
        );
    }

    @Test
    void scenario2() {
        Person person = new Person("abc", 7, "def@example.com");
        persistenceContext.addEntity(person);

        Person fetchedPerson = (Person) persistenceContext.getEntity(Person.class, 1L);
        persistenceContext.removeEntity(fetchedPerson);

        assertThrows(ObjectNotFoundException.class, () ->
                persistenceContext.addEntity(new Person(fetchedPerson.getId(), "newname", 8, "newemail@test.com"))
        );
    }

    @Test
    @DisplayName("1st level 캐시에 없는 row을 업데이트하고, 한번더 업데이트해보기")
    void scenario3() {
        loggingJdbcTemplate.execute("INSERT INTO users (id, nick_name, old, email) VALUES (20, '가나다', 21, 'email@test.com')");

        persistenceContext.addEntity(new Person(20L, "가나다라", 22, "email2@test.com"));
        persistenceContext.getEntity(Person.class, 20L);

        persistenceContext.addEntity(new Person(20L, "가나다라마", 22, "email2@test.com"));

        assertThat(loggingJdbcTemplate.executedQueries).containsExactly(
                "INSERT INTO users (id, nick_name, old, email) VALUES (20, '가나다', 21, 'email@test.com')", // loggingJdbcTemplate.execute
                "SELECT id, nick_name, old, email FROM users WHERE id = 20", // addEntity 에서 sync
                "UPDATE users SET nick_name = '가나다라', old = 22, email = 'email2@test.com' WHERE id = 20", // 첫번째 addEntity
                "UPDATE users SET nick_name = '가나다라마' WHERE id = 20" // 두번째 addEntity
        );
    }

    @Test
    @DisplayName("지운 걸 또 지워보기")
    void scenario4() {
        Person person = new Person("가나다라", 22, "email2@test.com");
        persistenceContext.addEntity(person);

        Person fetchedPerson = (Person) persistenceContext.getEntity(Person.class, 1L);

        persistenceContext.removeEntity(fetchedPerson);
        assertThrows(ObjectNotFoundException.class, () -> persistenceContext.removeEntity(fetchedPerson));
        System.out.println(loggingJdbcTemplate.executedQueries);

        assertThat(loggingJdbcTemplate.executedQueries).containsExactly(
                "INSERT INTO users (nick_name, old, email) VALUES ('가나다라', 22, 'email2@test.com')",
                "SELECT max(id) as id FROM users",
                "SELECT id, nick_name, old, email FROM users WHERE id = 1",
                "DELETE FROM users WHERE id = 1"
        );
    }

    @Test
    @DisplayName("존재하지 않는 객체를 업데이트하려고 해본다")
    void scenario5() {
        Person person = new Person(20L, "가나다라", 22, "email2@test.com");
        assertThrows(UnsupportedOperationException.class, () -> persistenceContext.addEntity(person));
    }

}
