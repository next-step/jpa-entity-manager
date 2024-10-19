package persistence.sql.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.domain.Person;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistenceContextImplTest {

    private PersistenceContext persistenceContext;
    Person person;

    @BeforeEach
    void init() {
        persistenceContext = new PersistenceContextImpl();
        person = new Person(1L, "yang", 23, "rhfp@naver.com");
    }

    @Test
    @DisplayName("PersistenceContext getEntity구현")
    void persistenceContext_find() {
        persistenceContext.addEntity(person);
        Person actual = persistenceContext.getEntity(Person.class, person.getId());

        assertEquals(person.getId(), actual.getId());
    }

    @Test
    @DisplayName("PersistenceContext add구현")
    void persistenceContext_add() {
        Person expectPerson = new Person(2L, "hong", 33, "test@naver.com");
        persistenceContext.addEntity(expectPerson);

        assertTrue(persistenceContext.containsEntity(Person.class, 2L));
    }

    @Test
    @DisplayName("PersistenceContext remove구현")
    void persistenceContext_remove() {
        persistenceContext.addEntity(person);
        persistenceContext.removeEntity(Person.class, person.getId());

        assertFalse(persistenceContext.containsEntity(Person.class, person.getId()));
    }

    @Test
    @DisplayName("PersistenceContext contains구현")
    void persistenceContext_contains() {
        persistenceContext.addEntity(person);

        assertAll(
                () -> assertTrue(persistenceContext.containsEntity(Person.class, 1L)),
                () -> assertFalse(persistenceContext.containsEntity(Person.class, 2L))
        );
    }
}
