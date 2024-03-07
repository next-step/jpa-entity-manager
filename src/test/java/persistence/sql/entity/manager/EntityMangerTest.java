package persistence.sql.entity.manager;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.db.H2Database;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMangerTest extends H2Database {

    private Person person;

    @BeforeEach
    void setUp() {
        this.person = new Person(1L, "박재성", 10, "jason");

        entityManager.removeAll(Person.class);
        entityManager.persist(person);
    }

    @DisplayName("디비를 조회하여, 한건의 결과를 반환한다.")
    @Test
    void findTest() {
        Person actual = entityManager.find(Person.class, 1L);

        assertThat(person).isEqualTo(actual);
    }

    @DisplayName("디비에 데이터가 저장이된다.")
    @Test
    void insertTest() {
        Person newPerson = new Person(2L, "이동규", 11, "cu");
        entityManager.persist(newPerson);

        Person findPerson = entityManager.find(Person.class, 2L);
        assertThat(findPerson).isEqualTo(newPerson);
    }

    @DisplayName("디비 데이터가 업데이트가 된다.")
    @Test
    void updateTest() {
        Person updatePerson = new Person(person.getId(), "이동규", 20, "cu");
        entityManager.merge(updatePerson);

        Person findPerson = entityManager.find(Person.class, person.getId());
        assertThat(findPerson).isEqualTo(updatePerson);
    }

    @DisplayName("디비에 데이터가 삭제가 된다.")
    @Test
    void deleteTest() {
        entityManager.remove(person);

        Optional<Person> optionalPerson = Optional.ofNullable(entityManager.find(Person.class, person.getId()));

        assertThat(optionalPerson.isPresent()).isFalse();
    }

}
