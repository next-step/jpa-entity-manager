package persistence.entity;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTestBase;
import persistence.Fixtures;

import static org.assertj.core.api.Assertions.assertThat;

class SimplePersistenceContextTest extends DatabaseTestBase {

    @Test
    @DisplayName("addEntity() 메서드 테스트")
    void addEntity() {
        Person person1 = Fixtures.person1();
        persistenceContext.addEntity(person1.getId(), person1);

        Person persist1 = (Person) persistenceContext.getEntity(person1.getId());

        assertThat(persist1).usingRecursiveComparison().isEqualTo(person1);
    }

    @Test
    @DisplayName("removeEntity() 메서드 테스트")
    void removeEntity() {
        Person person = entityManager.find(Person.class, 1L);
        persistenceContext.addEntity(person.getId(), person);
        persistenceContext.removeEntity(person);

        Person persist1 = (Person) persistenceContext.getEntity(person.getId());

        assertThat(persist1).isNull();
    }

    @Test
    @DisplayName("getDatabaseSnapshot() 메서드 테스트")
    void getDatabaseSnapshot() {
        Person person = entityManager.find(Person.class, 1L);
        persistenceContext.addEntity(person.getId(), person);
        persistenceContext.addEntitySnapshot(person.getId(), person);

        person.setName("new name");
        Person persist1 = (Person) persistenceContext.getDatabaseSnapshot(person.getId(), person);

        assertThat(persist1.getName()).isEqualTo(person.getName());
    }

}