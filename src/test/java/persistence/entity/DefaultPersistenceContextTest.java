package persistence.entity;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import sample.domain.Person;

class DefaultPersistenceContextTest {

    @Test
    @DisplayName("[성공] 영속성 컨텍스트에서 Person Entity 조회")
    void getEntity() {
        PersistenceContext context = new DefaultPersistenceContext();
        context.addEntity(person());

        context.getEntity(1L, Person.class)
                .ifPresentOrElse(personEntity -> assertAll("Person Entity 조회",
                        () -> assertEquals(personEntity.getId(), 1L),
                        () -> assertEquals(personEntity.getAge(), 20),
                        () -> assertEquals(personEntity.getEmail(), "person@email.com")
                ),
                () -> fail("Person entity not found in the persistence context.")
        );
    }

    @Test
    @DisplayName("[성공] 영속성 컨텍스트에 Person Entity 추가")
    void addEntity() {
        PersistenceContext context = new DefaultPersistenceContext();
        assertDoesNotThrow(() -> context.addEntity(person()));
    }

    @Test
    @DisplayName("[성공] 영속성 컨텍스트에 존재하는 Person Entity 제거")
    void removeEntity() {
        PersistenceContext context = new DefaultPersistenceContext();
        Person person = person();
        context.addEntity(person);

        context.removeEntity(person);

        assertFalse(context.getEntity(1L, Person.class).isPresent());
    }

    private Person person() {
        return new Person(1L, "person name", 20, "person@email.com");
    }

}
