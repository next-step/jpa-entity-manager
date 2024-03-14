package persistence;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceContextTest {

    private final DefaultPersistenceContext persistenceContext = new DefaultPersistenceContext();

    @Test
    void addEntity() {
        // given
        long id = 1L;
        Person entity = new Person(id, "name", 26, "email", 1);

        // when
        persistenceContext.addEntity(id, entity);

        // then
        Object result = persistenceContext.getEntity(id);
        assertThat(result).isEqualTo(entity);
    }

    @Test
    @DisplayName("persistenceContext 에 아무것도 저장되지 않았다면 null 을 반환한다.")
    void getEntity_1() {
        // given
        long id = 1L;

        // when
        Object result = persistenceContext.getEntity(id);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("해당 id 로 저장된 entity 가 없다면 null 을 반환한다.")
    void getEntity_2() {
        // given
        long id = 1L;
        Person entity = new Person(id, "name", 26, "email", 1);
        persistenceContext.addEntity(id, entity);

        // when
        Object result = persistenceContext.getEntity(2L);

        // then
        assertThat(result).isNull();
    }
}