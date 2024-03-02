package persistence.entity.persistencecontext;

import domain.Person;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.entity.EntityEntry;
import persistence.fixture.PersonFixture;

@DisplayName("SimplePersistenceContext class 의")
class SimplePersistenceContextTest {
    private SimplePersistenceContext context;
    private Person person;
    @BeforeEach
    void setUp() {
        context = new SimplePersistenceContext();
        person = PersonFixture.createPerson();
    }

    @DisplayName("addEntity 메서드는")
    @Nested
    class AddEntity {
        @DisplayName("entity를 캐시한다.")
        @Test
        void testAddEntity() {
            // when
            context.addEntity(person);

            // then
            Person person1 = (Person) context.getEntity(Person.class, person.getId());
            assertEquals(person, person1);
        }
    }

    @DisplayName("removeEntity 메서드는")
    @Nested
    class RemoveEntity {
        @DisplayName("entity를 캐시에서 제거한다.")
        @Test
        void testRemoveEntity() {
            // given
            context.addEntity(person);

            // when
            context.removeEntity(person);

            // then
            Person person1 = (Person) context.getEntity(Person.class, person.getId());
            assertNull(person1);
        }
    }
}
