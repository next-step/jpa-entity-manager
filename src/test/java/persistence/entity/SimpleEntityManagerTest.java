package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTestBase;
import persistence.Fixtures;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    @DisplayName("remove() 메서드 @Id Exception 테스트")
    void removeIdNotFoundException() {
        TestPerson person = new TestPerson();

        assertThatThrownBy(() -> entityManager.remove(person))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("No @Entity annotation");
    }

    @Test
    @DisplayName("merge() 메서드 테스트")
    void merge() {
        Person person = entityManager.find(Person.class, 1L);
        person.setName("new name");

        Person mergedPerson = entityManager.merge(person);

        assertThat(person.getName()).isEqualTo(mergedPerson.getName());
    }

    @Test
    @DisplayName("merge() 메서드 수정 사항이 없는 케이스 테스트")
    void mergeNoUpdate() {
        Person person = entityManager.find(Person.class, 1L);

        Person mergedPerson = entityManager.merge(person);

        assertThat(person.getName()).isEqualTo(mergedPerson.getName());
    }

    private class TestPerson {

        private Long id;

    }

}
