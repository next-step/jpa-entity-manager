package persistence.entity;

import database.sql.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static persistence.entity.EntityTestUtils.*;

class EntityManagerImplTest extends H2DatabaseTest {
    private EntityManagerImpl entityManager;

    @BeforeEach
    void setUp() {
        entityManager = new EntityManagerImpl(jdbcTemplate);
    }

    @Test
    void findMissingRecord() {
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                entityManager.find(Person.class, 1L)
        );
        assertThat(exception.getMessage()).isEqualTo("Expected 1 result, got 0");
    }

    @Test
    void find() {
        Person person = newPerson(null, "abc123", 14, "c123@d.com");
        entityManager.persist(person);

        Person found = entityManager.find(Person.class, 1L);

        assertSamePerson(found, person, false);
    }

    @Test
    void persistNewRow() {
        Person person = newPerson(null, "abc123", 14, "c123@d.com");
        entityManager.persist(person);
        Person person2 = newPerson(null, "zzzzzz", 44, "zzzzz@d.com");
        entityManager.persist(person2);

        List<Person> people = findPeople(jdbcTemplate);
        assertThat(people).hasSize(2);
        assertSamePerson(people.get(0), person, false);
        assertSamePerson(people.get(1), person2, false);
    }

    @Test
    void persistToUpdate() {
        Person person = newPerson(null, "abc123", 14, "c123@d.com");
        entityManager.persist(person);

        Person personToUpdate = newPerson(getLastSavedId(jdbcTemplate), "abc123", 15, "zzzzz@d.com");
        entityManager.persist(personToUpdate);

        List<Person> people = findPeople(jdbcTemplate);
        assertThat(people).hasSize(1);
        assertSamePerson(people.get(0), personToUpdate, true);
    }

    @Test
    void remove() {
        Person person = newPerson(null, "abc123", 14, "c123@d.com");
        entityManager.persist(person);

        entityManager.remove(entityManager.find(Person.class, getLastSavedId(jdbcTemplate)));

        List<Person> people = findPeople(jdbcTemplate);
        assertThat(people).hasSize(0);
    }
}
