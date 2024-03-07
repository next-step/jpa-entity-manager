package persistence.sql.entity.persister;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.db.H2Database;

import static org.assertj.core.api.Assertions.assertThat;

class EntityPersisterTest extends H2Database {

    private Person person;
    private Person person2;


    @BeforeEach
    void setUp() {
        this.person = new Person(1L, "박재성", 10, "jason");
        this.person2 = new Person(2L, "이동규", 20, "cu");

        entityManager.removeAll(Person.class);
        entityPersister.insert(person);
    }

    @DisplayName("데이터가 수정이 잘되는지 확인한다.")
    @Test
    void updateTest() {
        Person newPerson = new Person(person.getId(), "이동규", 20, "cu");

        entityPersister.update(newPerson);

        final Person result = entityManager.find(Person.class, person.getId());

        assertThat(newPerson).isEqualTo(result);
    }

    @DisplayName("Person 데이터 추가가 된다.")
    @Test
    void insertTest() {
        entityPersister.insert(person2);

        final Person result = entityManager.find(Person.class, person2.getId());

        assertThat(person2).isEqualTo(result);
    }

    @DisplayName("Person id값 없는것도 저장이 된다.")
    @Test
    void isExistsInsert() {
        Person personWithNotExistId = new Person(null, "테스트", 123, "test@nextstep.com");

        Object key = entityPersister.insertWithPk(personWithNotExistId);

        assertThat(key).isEqualTo(2L);
    }

    @DisplayName("Person 데이터를 삭제한다.")
    @Test
    void deleteTest() {
        entityPersister.delete(person);

        final Person result = entityManager.find(Person.class, person.getId());

        assertThat(result).isNull();
    }

}
