package persistence.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.Person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@Entity
class TestEntity {
    @Id
    private Long id;

    public TestEntity() {
    }

    public TestEntity(Long id) {
        this.id = id;
    }
}

class PersistenceContextImplTest {
    final private PersistenceContext persistContext = new PersistenceContextImpl();

    @Test
    @DisplayName("엔티티 제거")
    void testGetEntity() {
        Long id = 1L;
        Person person = new Person(id, "nick_name", 10,  "email", null);
        EntityKey entityKey = new EntityKey(Person.class, id);
        persistContext.addEntity(entityKey, person);

        persistContext.removeEntity(person);

        assertThat(persistContext.getEntity(entityKey)).isNull();
    }

    @Test
    @DisplayName("요구사항2: dirty check 스냅샷과 다를시 true 반환")
    void testIsDirty() {
        Long id = 1L;
        Person person = new Person(id, "nick_name", 10, "email", null);
        EntityKey entityKey = new EntityKey(Person.class, id);

        persistContext.addEntity(entityKey, person);
        person.changeName("new_name");

        assertThat(persistContext.isDirty(entityKey, person)).isTrue();
    }

    @Test
    @DisplayName("요구사항2: dirty check 스냅샷과 같을시 false 반환")
    void testIsNotDirty() {
        Long id = 1L;
        Person person = new Person(id, "nick_name", 10, "email", null);
        EntityKey entityKey = new EntityKey(Person.class, id);

        persistContext.addEntity(entityKey, person);

        assertThat(persistContext.isDirty(entityKey, person)).isFalse();
    }

    @Test
    @DisplayName("id가 같고 entity 가 다를때도 식별해서 가져올수있다.")
    void testGetSameIdButDifferentEntity() {
        Long id = 1L;
        Person person = new Person(id, "nick_name", 10, "email", null);
        TestEntity testEntity = new TestEntity(id);
        EntityKey personKey = new EntityKey(Person.class, id);
        EntityKey testEntityKey = new EntityKey(TestEntity.class, id);
        persistContext.addEntity(personKey, person);
        persistContext.addEntity(testEntityKey, testEntity);

        Object foundPerson = persistContext.getEntity(personKey);
        Object foundTestEntity = persistContext.getEntity(testEntityKey);

        assertThat(foundPerson).isNotEqualTo(foundTestEntity);
    }
}
