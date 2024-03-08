package persistence.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.study.sql.ddl.Person3;

import static org.assertj.core.api.Assertions.assertThat;

class SimplePersistenceContextTest {

    private final PersistenceContext persistenceContext = new SimplePersistenceContext();
    private final Person3 person = new Person3(1L, "qwer1", 123, "email1@email.com");

    @BeforeEach
    void setUp() {
        EntityId id = new EntityId(1L);
        persistenceContext.addEntity(id, person);
    }

    @DisplayName("1차 캐시에서 엔티티를 정상적으로 가져온다.")
    @Test
    void getEntity() {
        EntityId id = new EntityId(1L);
        Object result = persistenceContext.getEntity(Person3.class, id);

        assertThat(result).isEqualTo(person);
    }

    @DisplayName("엔티티를 persistence context의 1차 캐시에 추가할 수 있다.")
    @Test
    void addEntity() {
        Person3 person = new Person3(2L, "qwer", 123, "email@email.com");

        EntityId id = new EntityId(2L);
        persistenceContext.addEntity(id, person);

        Object result = persistenceContext.getEntity(Person3.class, id);
        assertThat(result).isEqualTo(person);
    }

    @DisplayName("1차 캐시에서 엔티티가 정상적으로 제거된다.")
    @Test
    void removeEntity() {
        persistenceContext.removeEntity(person);

        EntityId id = new EntityId(1L);
        Object result = persistenceContext.getEntity(Person3.class, id);
        assertThat(result).isNull();
    }

    @DisplayName("캐시에 넣은 객체와 꺼낸 객체의 동일성이 보장된다.")
    @Test
    void identity() {
        Person3 person = new Person3(2L, "qwer1", 123, "email1@email.com");

        EntityId id = new EntityId(2L);
        persistenceContext.addEntity(id, person);
        Object result = persistenceContext.getEntity(Person3.class, id);

        assertThat(person == result).isTrue();
    }

    @Test
    void getDatabaseSnapshot() {
        Object result = persistenceContext.getDatabaseSnapshot(new EntityId(1L), person);

        assertThat(result == person).isTrue();
    }
}
