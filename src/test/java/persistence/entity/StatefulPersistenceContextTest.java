package persistence.entity;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static persistence.entity.PersistenceContext.NO_ROW;

class StatefulPersistenceContextTest {
    private StatefulPersistenceContext persistenceContext;
    private Person person;
    private EntityKey entityKey;

    @BeforeEach
    void beforeEach() {
        this.persistenceContext = new StatefulPersistenceContext();
        this.person = new Person(1L, "slow", 20, "email@email.com", 1);
        this.entityKey = EntityKey.of(1L, person.getClass().getSimpleName());
    }

    @Test
    @DisplayName("getDatabaseSnapshot 항상 최신의 상태를 반환한다")
    void getDatabaseSnapshot() {
        assertThat(persistenceContext.getDatabaseSnapshot(1L, person)).isEqualTo(person);
        assertThat(persistenceContext.getCached(entityKey)).isEqualTo(person);
    }

    @Test
    @DisplayName("getCachedDatabaseSnapshot snapshot 이 없을때는 빈객체타입을 반환한다")
    void cachedDatabaseSnapshot() {
        assertThat(persistenceContext.getCachedDatabaseSnapshot(entityKey)).isEqualTo(NO_ROW);

    }
}
