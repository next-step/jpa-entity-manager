package persistence.sql.entity.loader;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.db.H2Database;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EntityLoaderImplTest extends H2Database {

    private Person person1;
    private Person person2;


    @BeforeEach
    void setUp() {
        this.person1 = new Person(1L, "박재성", 10, "jason");
        this.person2 = new Person(2L, "이동규", 20, "cu");

        entityPersister.deleteAll(Person.class);
        entityPersister.insert(person1);
        entityPersister.insert(person2);
    }

    @DisplayName("아이디가 있는 값을 조회후, 엔티티로 반환한다.")
    @Test
    void findByIdTest() {
        Person person = entityLoader.find(Person.class, person1.getId());

        assertThat(person).isEqualTo(person1);
    }

    @DisplayName("아이디값이 없을 시, 널로 반환한다.")
    @Test
    void isNotExistsFindById() {
        Person person = entityLoader.find(Person.class, 3);

        assertThat(person).isNull();
        ;
    }

    @DisplayName("엔티티에 매핑된 테이블 값을 모두 조회한다.")
    @Test
    void findAllTest() {
        List<Person> persons = entityLoader.findAll(Person.class);

        assertThat(persons).containsExactly(person1, person2);
    }


}