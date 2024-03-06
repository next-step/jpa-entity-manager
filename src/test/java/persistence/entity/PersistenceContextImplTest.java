package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Person;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceContextImplTest {
    final private PersistenceContext persistContext = new PersistenceContextImpl();

    @Test
    @DisplayName("엔티티 제거")
    void testGetEntity() {
        Long id = 1L;
        Person person = new Person(id, "nick_name", 10,  "email", null);
        persistContext.addEntity(id, person);

        persistContext.removeEntity(person);

        assertThat(persistContext.getEntity(id)).isNull();
    }

    @Test
    @DisplayName("요구사항2: dirty check 스냅샷과 다를시 true 반환")
    void testIsDirty() {
        Long id = 1L;
        Person person = new Person(id, "nick_name", 10, "email", null);
        persistContext.addEntity(id, person);
        person.changeName("new_name");

        assertThat(persistContext.isDirty(id, person)).isTrue();
    }

    @Test
    @DisplayName("요구사항2: dirty check 스냅샷과 같을시 false 반환")
    void testIsNotDirty() {
        Long id = 1L;
        Person person = new Person(id, "nick_name", 10, "email", null);
        persistContext.addEntity(id, person);

        assertThat(persistContext.isDirty(id, person)).isFalse();
    }
}
