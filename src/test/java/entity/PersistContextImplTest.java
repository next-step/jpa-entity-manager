package entity;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.PersistContext;
import persistence.entity.PersistContextImpl;

import static entity.fixtures.PersonFixtures.완벽한_사람_객체;
import static org.junit.jupiter.api.Assertions.*;

class PersistContextImplTest {
    @Test
    @DisplayName("영속성 context Person Entity 조회")
    void SelectPerson() {
        PersistContext persistContext = new PersistContextImpl();
        persistContext.addEntity(완벽한_사람_객체(1L, "장현준", 29, "qwerty@naver.com", 0));

        persistContext.getEntity(1L, Person.class).ifPresentOrElse(person -> assertAll("Person Entity Select",
                () -> assertEquals(person.getId(), 1L),
                () -> assertEquals(person.getAge(), 29)), () -> fail("Person Entity Not Found")
        );
    }
}