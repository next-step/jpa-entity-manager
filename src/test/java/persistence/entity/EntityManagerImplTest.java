package persistence.entity;

import database.sql.Person;
import database.sql.dml.NoAutoIncrementUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistence.entity.database.PrimaryKeyMissingException;
import testsupport.H2DatabaseTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static testsupport.EntityTestUtils.*;

class EntityManagerImplTest extends H2DatabaseTest {
    private EntityManagerImpl entityManager;

    @BeforeEach
    void setUp() {
        entityManager = EntityManagerImpl.from(loggingJdbcTemplate);
    }

    @Test
    void findMissingRecord() {
        assertThat(entityManager.find(Person.class, 1L)).isNull();
    }

    @Test
    void persistAndFind() {
        Person person = newPerson(null, "abc123", 14, "c123@d.com");
        entityManager.persist(person);

        Person found = entityManager.find(Person.class, 1L);

        assertSamePerson(found, person, false);
    }

    @Test
    void persistNoAutoIncrementEntityWithoutId() {
        NoAutoIncrementUser user = new NoAutoIncrementUser(null, "abc123", 14, "c123@d.com");

        PrimaryKeyMissingException ex = assertThrows(PrimaryKeyMissingException.class, () -> entityManager.persist(user));
        assertThat(ex.getMessage()).isEqualTo("Primary key is not assigned when inserting: database.sql.dml.NoAutoIncrementUser");
    }

    @Test
    void persistNewRow() {
        Person person = newPerson(null, "abc123", 14, "c123@d.com");
        entityManager.persist(person);
        Person person2 = newPerson(null, "zzzzzz", 44, "zzzzz@d.com");
        entityManager.persist(person2);

        List<Person> people = findPeople(loggingJdbcTemplate);
        assertThat(people).hasSize(2);
        assertSamePerson(people.get(0), person, false);
        assertSamePerson(people.get(1), person2, false);
    }

    @Test
    void persistToUpdate() {
        Person person = newPerson(null, "abc123", 14, "c123@d.com");
        entityManager.persist(person);

        Person personToUpdate = newPerson(getLastSavedId(loggingJdbcTemplate), "abc123", 15, "zzzzz@d.com");
        entityManager.persist(personToUpdate);
        entityManager.persist(personToUpdate);

        List<Person> people = findPeople(loggingJdbcTemplate);
        assertThat(people).hasSize(1);
        assertSamePerson(people.get(0), personToUpdate, true);
    }

    @Test
    void remove() {
        Person person = newPerson(null, "abc123", 14, "c123@d.com");
        entityManager.persist(person);

        Person person1 = entityManager.find(Person.class, getLastSavedId(loggingJdbcTemplate));
        entityManager.remove(person1);

        List<Person> people = findPeople(loggingJdbcTemplate);
        assertThat(people).hasSize(0);
    }
}
