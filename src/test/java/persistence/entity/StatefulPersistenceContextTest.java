package persistence.entity;

import domain.Person;
import domain.PersonFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StatefulPersistenceContextTest {
    private PersistenceContext context;
    private Person person;

    @BeforeEach
    void setUp() {
        context = new StatefulPersistenceContext();
        person = PersonFixture.createPerson();
    }

    @Test
    void persistEntity() {
        context.persistEntity(person);
        assertThat(
                context.hasEntity(new EntityKey(person))
        ).isTrue();
    }

    @Test
    void findEntity() {
        context.persistEntity(person);
        assertThat(
                person == context.findEntity(new EntityKey(person))
        ).isTrue();
    }

    @Test
    void removeEntity() {
        context.persistEntity(person);
        context.removeEntity(person);
        assertThat(
                context.hasEntity(new EntityKey(person))
        ).isFalse();
    }
}
