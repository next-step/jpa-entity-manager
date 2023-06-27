package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.Person;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceContextTest {

    private final PersistenceContext persistenceContext = new PersistenceContextImpl();

    @DisplayName("영속성 컨텍스트에 엔티티를 저장한다.")
    @Test
    void addEntity() {
        Person person = new Person(1L, "jack", 20, "jack@abc.com");
        persistenceContext.addEntity(person.getId(), person);
        Object entity = persistenceContext.getEntity(person.getId());
        assertThat(entity).isEqualTo(person);
    }

    @DisplayName("영속성 컨텍스트에서 엔티티를 제거한다.")
    @Test
    void removeEntity() {
        Person person = new Person(1L, "jack", 20, "jack@abc.com");
        persistenceContext.addEntity(person.getId(), person);
        persistenceContext.removeEntity(person.getId());
        assertThat(persistenceContext.getEntity(person.getId())).isNull();
    }
}