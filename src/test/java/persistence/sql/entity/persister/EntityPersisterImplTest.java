package persistence.sql.entity.persister;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;
import persistence.sql.db.H2Database;
import persistence.sql.entity.manager.EntityManagerImpl;
import persistence.sql.entity.manager.EntityManger;

import static org.assertj.core.api.Assertions.assertThat;

class EntityPersisterImplTest extends H2Database {

    private EntityPersister<Person, Long> entityPersister;
    private EntityManger<Person, Long> entityManager;
    private Person person;
    private Person person2;


    @BeforeEach
    void setUp() {
        this.entityManager = new EntityManagerImpl<>(jdbcTemplate, Person.class);
        this.entityPersister = new EntityPersisterImpl<>(jdbcTemplate, Person.class);

        this.person = new Person(1L, "박재성", 10, "jason");
        this.person2 = new Person(2L, "이동규", 20, "cu");

        entityPersister.deleteAll();
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

    @DisplayName("Person 데이터를 삭제한다.")
    @Test
    void deleteTest() {
        entityPersister.delete(person.getId());

        final Person result = entityManager.find(Person.class, person.getId());

        assertThat(result).isNull();
    }

}
