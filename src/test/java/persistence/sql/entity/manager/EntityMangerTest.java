package persistence.sql.entity.manager;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.db.H2Database;
import persistence.sql.entity.exception.ReadOnlyException;
import persistence.sql.entity.exception.RemoveEntityException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @DisplayName("디비를 삭제후, 조회를 하면 에러를 반환한다.")
    @Test
    void isGoneFindTest() {
        entityManager.remove(person);

        assertThatThrownBy(() -> entityManager.find(Person.class, person.getId()))
                .isInstanceOf(RemoveEntityException.class)
                .hasMessage("삭제된 엔티티입니다.");
    }

    @DisplayName("디비에 데이터가 저장이된다.")
    @Test
    void insertTest() {
        Person newPerson = new Person(2L, "이동규", 11, "cu");
        entityManager.persist(newPerson);

        Person findPerson = entityManager.find(Person.class, 2L);
        assertThat(findPerson).isEqualTo(newPerson);
    }

    @DisplayName("아이디가 없는값도 저장이 된다.")
    @Test
    void notExistsIdSave() {
        Person newPerson = new Person(null, "김성주", 14, "jpa");

        entityManager.persist(newPerson);

        assertThat(entityManager.findAll(Person.class)).hasSize(2);
    }

    @DisplayName("readOnly로 조회할시, 업데이트시 에러를 반환한다.")
    @Test
    void readOnlyUpdateTest() {
        Person readOnlyPerson = entityManager.findOfReadOnly(Person.class, person.getId());

        assertThatThrownBy(() -> entityManager.persist(readOnlyPerson))
                .isInstanceOf(ReadOnlyException.class)
                .hasMessage("읽기전용은 수정 및 삭제가 불가능 합니다.");
    }

    @DisplayName("디비 데이터가 업데이트가 된다.")
    @Test
    void updateTest() {
        Person updatePerson = new Person(person.getId(), "이동규", 20, "cu");
        entityManager.persist(updatePerson);

        Person findPerson = entityManager.find(Person.class, person.getId());
        assertThat(findPerson).isEqualTo(updatePerson);
    }

    @DisplayName("디비에 데이터가 삭제가 된다.")
    @Test
    void deleteTest() {
        entityManager.remove(person);

        assertThatThrownBy(() -> entityManager.find(Person.class, person.getId()))
                .isInstanceOf(RemoveEntityException.class)
                .hasMessage("삭제된 엔티티입니다.");
    }

    @DisplayName("readOnly로 조회된 값은 삭제시 에러를 반환한다.")
    @Test
    void readOnlyRemoveTest() {
        Person readOnlyPerson = entityManager.findOfReadOnly(Person.class, person.getId());

        assertThatThrownBy(() -> entityManager.persist(readOnlyPerson))
                .isInstanceOf(ReadOnlyException.class)
                .hasMessage("읽기전용은 수정 및 삭제가 불가능 합니다.");
    }
}
