package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTestBase;
import persistence.Fixtures;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleEntityManagerTest extends DatabaseTestBase {

    @Test
    @DisplayName("find() 메서드 테스트")
    void find() {
        Person person = entityManager.find(Person.class, 1L);

        assertThat(person.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("persist() 메서드 테스트")
    void persist() {
        Person person = Fixtures.person2();

        Person persist = entityManager.persist(person);

        assertThat(person.getName()).isEqualTo(persist.getName());
        assertThat(person.getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("remove() 메서드 테스트")
    void remove() {
        Person person = entityManager.find(Person.class, 1L);
        entityManager.remove(person);

        Person removedPerson = entityManager.find(Person.class, 1L);
        assertThat(removedPerson).isNull();
    }

}
