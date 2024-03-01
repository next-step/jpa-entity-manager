package persistence.entity;

import database.sql.Person;
import org.junit.jupiter.api.Test;
import testsupport.H2DatabaseTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static testsupport.EntityTestUtils.assertSamePerson;

class PersistenceContextImplTest extends H2DatabaseTest {

    @Test
    void scenario1() {
        PersistenceContext persistenceContext = new PersistenceContextImpl(loggingJdbcTemplate);

        Person person = new Person("abc", 7, "def@example.com");
        persistenceContext.addEntity(person);

        Person fetchedPerson = (Person) persistenceContext.getEntity(Person.class, 1L);
        persistenceContext.getEntity(Person.class, 1L);
        persistenceContext.getEntity(Person.class, 1L);
        persistenceContext.getEntity(Person.class, 1L);

        persistenceContext.removeEntity(fetchedPerson);

        assertAll(
                () -> assertSamePerson(fetchedPerson, person, false),
                () -> assertThat(persistenceContext.getEntity(Person.class, 1L)).isNull(),
                () -> assertThat(loggingJdbcTemplate.executedQueries).containsExactly(
                        "INSERT INTO users (nick_name, old, email) VALUES ('abc', 7, 'def@example.com')",
                        "SELECT max(id) as id FROM users",
                        "SELECT id, nick_name, old, email FROM users WHERE id = 1",
                        "DELETE FROM users WHERE id = 1",
                        "SELECT id, nick_name, old, email FROM users WHERE id = 1"
                )
        );
    }
}
