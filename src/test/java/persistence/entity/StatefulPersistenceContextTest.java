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
        context.addEntity(person);
        assertThat(
                context.hasEntity(new EntityKey(person))
        ).isTrue();
    }

    @Test
    void findEntity() {
        context.addEntity(person);
        assertThat(
                person == context.getEntity(new EntityKey(person))
        ).isTrue();
    }

    @Test
    void removeEntity() {
        context.addEntity(person);
        context.removeEntity(person);
        assertThat(
                context.hasEntity(new EntityKey(person))
        ).isFalse();
    }
}
