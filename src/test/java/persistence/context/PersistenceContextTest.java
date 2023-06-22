package persistence.context;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.model.Person;

import static fixture.PersonFixtures.createPerson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PersistenceContextTest {
    private final PersistenceContext persistenceContext = new BasicPersistentContext();

    @Test
    @DisplayName("영속성 컨텍스트에 엔티티를 추가하고 조회한다")
    void addEntityAndFind() {
        // given
        Person person = createPerson();

        // when
        persistenceContext.addEntity(1L, person);
        Object persistedEntity = persistenceContext.getEntity(1L);

        // then
        assertAll(
                () -> assertThat(persistedEntity).isEqualTo(person),
                () -> assertThat(persistedEntity == person).isTrue()
        );
    }

    @Test
    @DisplayName("영속성 컨텍스트에 엔티티를 제거한다")
    void remove() {
        // given
        Person person = createPerson();
        persistenceContext.addEntity(1L, person);
        Object persistedEntity = persistenceContext.getEntity(1L);

        // when
        persistenceContext.removeEntity(1L);

        // then
        Object result = persistenceContext.getEntity(1L);
        assertThat(result).isEqualTo(null);
    }
}