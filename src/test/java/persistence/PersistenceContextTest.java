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

    @Test
    @DisplayName("persistenceContext 에 저장되어 있다면 조회된다.")
    void getEntity_3() {
        // given
        long id = 1L;
        Person entity = new Person(id, "name", 26, "email", 1);

        persistenceContext.addEntity(id, entity);

        // when
        Object result = persistenceContext.getEntity(id);

        // then
        assertThat(result).isEqualTo(entity);
    }

    @Test
    @DisplayName("persistenceContext 에 아무것도 저장되지 않았다면 null 을 반환한다.")
    void removeEntity_1() {
        // given
        long id = 1L;

        // when
        Object result = persistenceContext.removeEntity(id);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("저장된 entity 가 없다면 null 을 반환한다.")
    void removeEntity_2() {
        // given
        long id = 1L;
        Person entity = new Person(id, "name", 26, "email", 1);

        persistenceContext.addEntity(id, entity);

        // when
        Object result = persistenceContext.removeEntity(2L);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("저장된 entity 가 삭제된다면 해당 entity 를 반환한다.")
    void removeEntity_3() {
        // given
        long id = 1L;
        Person entity = new Person(id, "name", 26, "email", 1);

        persistenceContext.addEntity(id, entity);

        // when
        Object result = persistenceContext.removeEntity(id);

        // then
        assertThat(result).isEqualTo(entity);
    }

    @Test
    @DisplayName("이전에 저장된 값이 없는 경우 null 을 반환한다.")
    void getDatabaseSnapshot_1() {
        // given
        long id = 1L;
        Person entity = new Person(id, "name", 26, "email", 1);

        // when
        Object result = persistenceContext.getCachedDatabaseSnapshot(id, entity);

        // then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("이전에 저장된 값이 있는 경우 동일성이 보장된다")
    void getDatabaseSnapshot_2() {
        // given
        long id = 1L;
        Person entity = new Person(id, "name", 26, "email", 1);
        persistenceContext.getCachedDatabaseSnapshot(id, entity);

        // when
        Object result = persistenceContext.getCachedDatabaseSnapshot(id, entity);

        // then
        assertThat(result).isEqualTo(entity);
    }
}
