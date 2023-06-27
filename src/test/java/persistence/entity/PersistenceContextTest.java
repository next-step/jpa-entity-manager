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
        Long personId = 1L;
        Person person = new Person(personId, "jack", 20, "jack@abc.com");
        persistenceContext.addEntity(personId, person);
        Object entity = persistenceContext.getEntity(personId);
        assertThat(entity).isEqualTo(person);
    }

    @DisplayName("영속성 컨텍스트에서 엔티티를 제거한다.")
    @Test
    void removeEntity() {
        Long personId = 1L;
        Person person = new Person(personId, "jack", 20, "jack@abc.com");
        persistenceContext.addEntity(personId, person);
        persistenceContext.removeEntity(personId);
        assertThat(persistenceContext.getEntity(personId)).isNull();
    }
}
