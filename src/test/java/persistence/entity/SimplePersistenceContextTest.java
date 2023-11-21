package persistence.entity;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.DatabaseTestBase;

import static org.assertj.core.api.Assertions.assertThat;
import static persistence.entity.Status.MANAGED;

class SimplePersistenceContextTest extends DatabaseTestBase {

    @Test
    @DisplayName("addEntity() 메서드 테스트")
    void addEntity() {
        Person person = entityManager.find(Person.class, 1L);
        persistenceContext.addEntity(person.getId(), person, MANAGED);

        Person persist1 = (Person) persistenceContext.getEntity(person.getId());

        assertThat(persist1).usingRecursiveComparison().isEqualTo(person);
    }

    @Test
    @DisplayName("removeEntity() 메서드 테스트")
    void removeEntity() {
        Person person = entityManager.find(Person.class, 1L);
        persistenceContext.addEntity(person.getId(), person, MANAGED);
        persistenceContext.removeEntity(person);

        Person persist1 = (Person) persistenceContext.getEntity(person.getId());

        assertThat(persist1).isNull();
        assertThat(persistenceContext.getEntityStatus(person)).isEqualTo(Status.DELETED);
    }

    @Test
    @DisplayName("getDatabaseSnapshot() 메서드 테스트")
    void getDatabaseSnapshot() {
        Person person = entityManager.find(Person.class, 1L);
        persistenceContext.addEntity(person.getId(), person, MANAGED);
        persistenceContext.addEntitySnapshot(person.getId(), person);

        person.setName("new name");
        Person persist1 = (Person) persistenceContext.getDatabaseSnapshot(person.getId(), person);

        assertThat(persist1.getName()).isEqualTo(person.getName());
        assertThat(persistenceContext.getEntityStatus(persist1)).isEqualTo(MANAGED);
    }

}