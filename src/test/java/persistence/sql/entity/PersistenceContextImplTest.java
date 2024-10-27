package persistence.sql.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.domain.Person;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistenceContextImplTest {

    private PersistenceContext persistenceContext;
    Person person;

    @BeforeEach
    void init() {
        persistenceContext = new PersistenceContextImpl();
        person = new Person(1L, "yang", 23, "rhfp@naver.com");
    }

    @Test
    @DisplayName("PersistenceContext에서 getEntity메서드를 통해 엔티티를 반환한다.")
    void persistenceContext_find() {
        persistenceContext.addEntity(person, person.getId());
        Person actual = persistenceContext.getEntity(person.getClass(), person.getId());

        assertEquals(person.getId(), actual.getId());
    }

    @Test
    @DisplayName("PersistenceContext에서 addEntity메서드를 통해 엔티티를 추가한다.")
    void persistenceContext_add() {
        Person expectPerson = new Person(2L, "hong", 33, "test@naver.com");
        persistenceContext.addEntity(expectPerson, expectPerson.getId());
        EntityKey entityKey = new EntityKey(2L, expectPerson.getClass());
        assertTrue(persistenceContext.containsEntity(entityKey));
    }

    @Test
    @DisplayName("PersistenceContext에서 removeEntity메서드를 통해 엔티티를 제거한다.")
    void persistenceContext_remove() {
        persistenceContext.addEntity(person, person.getId());
        persistenceContext.removeEntity(Person.class, person.getId());
        EntityKey entityKey = new EntityKey(person.getId(), person.getClass());
        assertFalse(persistenceContext.containsEntity(entityKey));
    }

    @Test
    @DisplayName("PersistenceContext의 containsEntity메서드를 통해 관리되고있는 엔티티인지 확인한다.")
    void persistenceContext_contains() {
        Long notExistId = 2L;
        EntityKey entityKey = new EntityKey(person.getId(), person.getClass());
        EntityKey notExistEntityKey = new EntityKey(notExistId, person.getClass());

        persistenceContext.addEntity(person, person.getId());

        assertAll(
                () -> assertTrue(persistenceContext.containsEntity(entityKey)),
                () -> assertFalse(persistenceContext.containsEntity(notExistEntityKey))
        );
    }

    @Test
    @DisplayName("PersistenceContext의 getDatabaseSnapshot메서드를 스냅샷을 가져온다.")
    void persistenceContext_addSnapshot_and_getDatabaseSnapshot() {
        persistenceContext.addSnapshot(person.getId(), person);

        Person databaseSnapshot = (Person) persistenceContext.getDatabaseSnapshot(person.getId(), person);

        assertAll(
                () -> assertTrue(databaseSnapshot.getName().equals(person.getName())),
                () -> assertTrue(databaseSnapshot.getAge().equals(person.getAge())),
                () -> assertTrue(databaseSnapshot.getEmail().equals(person.getEmail()))
        );

    }

    @Test
    @DisplayName("엔티티의 변경이 있을시 isDirty메서드를 통해 더티체킹을 진행할 수 있따.")
    void persistenceContext_isDirty() {
        persistenceContext.addSnapshot(person.getId(), person);
        boolean isDirty = persistenceContext.isDirty(person.getId(), person);

        person = new Person(1L, "change name", 23, "rhfp@naver.com");
        boolean changeDirty = persistenceContext.isDirty(person.getId(), person);

        assertAll(
                () -> assertFalse(isDirty),
                () -> assertTrue(changeDirty)
        );
    }
}
