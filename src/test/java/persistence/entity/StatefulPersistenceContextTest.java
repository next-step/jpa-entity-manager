package persistence.entity;

import domain.Person;
import domain.PersonFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.exception.NoRowSnapshotException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StatefulPersistenceContextTest {
    private PersistenceContext context;
    private Person person;

    @BeforeEach
    void setUp() {
        context = new StatefulPersistenceContext();
        person = PersonFixture.createPerson();
    }

    @Test
    @DisplayName("addEntity 를 통해 entity 를 영속성 컨텍스트에 추가할 수 있다.")
    void addEntity() {
        context.addEntity(person);
        assertThat(
                context.hasEntity(new EntityKey(person))
        ).isTrue();
    }

    @Test
    @DisplayName("getEntity 를 통해 entity 를 영속성 컨텍스트에 조회할 수 있다.")
    void getEntity() {
        context.addEntity(person);
        assertThat(
                person == context.getEntity(new EntityKey(person))
        ).isTrue();
    }

    @Test
    @DisplayName("removeEntity 를 통해 entity 를 영속성 컨텍스트에서 삭제할 수 있다.")
    void removeEntity() {
        context.addEntity(person);
        context.removeEntity(person);
        assertThat(
                context.hasEntity(new EntityKey(person))
        ).isFalse();
    }

    @Test
    @DisplayName("캐싱되지 않은 snapshot 을 가져오려 하면 NoRowSnapshotException 이 발생한다.")
    void noRowSnapshot() {
        assertThatThrownBy(
                () -> context.getCachedDatabaseSnapshot(new EntityKey(person))
        ).isInstanceOf(NoRowSnapshotException.class);
    }

    @Test
    @DisplayName("getDatabaseSnapshot 을 통해 스냅샷을 저장하고, getCachedDatabaseSnapshot 로 스냅샷을 가져온다.")
    void getDatabaseSnapshot() {
        final EntityKey<Person> key = new EntityKey<>(person);
        assertThat(
                context.getDatabaseSnapshot(key, person)
                        == context.getCachedDatabaseSnapshot(key)
        ).isTrue();
    }
}
