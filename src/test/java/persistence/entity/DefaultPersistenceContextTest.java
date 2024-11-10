package persistence.entity;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.impl.DefaultPersistenceContext;
import sample.domain.Person;

class DefaultPersistenceContextTest {

    @Test
    @DisplayName("[성공] 영속성 컨텍스트에서 Person Entity 조회")
    void getEntity() {
        PersistenceContext context = new DefaultPersistenceContext();
        Person person = person(1L);
        context.addEntity(person, EntityStatus.MANAGED);
        assertEquals(context.getEntity(1L, Person.class).entity(), person);
    }

    @Test
    @DisplayName("[성공] 영속성 컨텍스트에 Person Entity 추가")
    void addEntity() {
        PersistenceContext context = new DefaultPersistenceContext();
        assertDoesNotThrow(() -> context.addEntity(person(), EntityStatus.MANAGED));
    }

    @Test
    @DisplayName("[성공] 영속성 컨텍스트에 존재하는 Person Entity 제거")
    void removeEntity() {
        PersistenceContext context = new DefaultPersistenceContext();
        Person person = person(1L);
        context.addEntity(person, EntityStatus.MANAGED);

        context.removeEntity(person);

        Assertions.assertThatThrownBy(() -> context.getEntity(1L, Person.class))
                .hasMessage("Not exist EntityEntry EntityKey: 1, EntityType: class sample.domain.Person");
    }

    private Person person() {
        return person(1L);
    }

    private Person person(Long id) {
        return new Person(id, "person name", 20, "person@email.com");
    }

}
