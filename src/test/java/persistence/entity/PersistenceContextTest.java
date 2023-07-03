package persistence.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.ddl.Person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PersistenceContextTest {

    private static final Long PERSON_JACK_ID = 1L;
    private static final Person PERSON_JACK = new Person(PERSON_JACK_ID, "jack", 20, "jack@abc.com");

    private PersistenceContext persistenceContext;

    @BeforeEach
    void init() {
        persistenceContext = new PersistenceContextImpl();
        persistenceContext.addEntity(PERSON_JACK_ID, PERSON_JACK);
    }


    @DisplayName("영속성 컨텍스트에서 엔티티를 조회한다.")
    @Test
    void getEntity() {
        Object entity = persistenceContext.getEntity(PERSON_JACK_ID);
        assertThat(entity).isEqualTo(PERSON_JACK);
    }

    @DisplayName("영속성 컨텍스트에 엔티티를 저장한다.")
    @Test
    void addEntity() {
        Person person = new Person(2L, "kevin", 30, "kevin@abc.com");
        persistenceContext.addEntity(person.getId(), person);
        Object entity = persistenceContext.getEntity(person.getId());
        assertThat(entity).isEqualTo(person);
    }

    @DisplayName("영속성 컨텍스트에서 엔티티를 제거한다.")
    @Test
    void removeEntity() {
        persistenceContext.removeEntity(PERSON_JACK_ID);
        assertThat(persistenceContext.getEntity(PERSON_JACK_ID)).isNull();
    }

    @DisplayName("영속성 컨텍스트에서 데이터베이스 스냅샷을 조회한다.")
    @Test
    void getDatabaseSnapshot() {
        Person snapshot = (Person) persistenceContext.getDatabaseSnapshot(PERSON_JACK_ID, PERSON_JACK);
        
        assertAll("데이터베이스 스냅샷 조회", () -> {
            assertThat(snapshot.getId()).isEqualTo(PERSON_JACK_ID);
            assertThat(snapshot.getName()).isEqualTo(PERSON_JACK.getName());
            assertThat(snapshot.getAge()).isEqualTo(PERSON_JACK.getAge());
            assertThat(snapshot.getEmail()).isEqualTo(PERSON_JACK.getEmail());
            assertThat(snapshot.getIndex()).isNull();
        });
    }

    @DisplayName("영속성 컨텍스트에서 캐싱된 데이터베이스 스냅샷을 조회한다.")
    @Test
    void getCachedDatabaseSnapshot() {
        Person snapshot = (Person) persistenceContext.getCachedDatabaseSnapshot(PERSON_JACK_ID);
        assertThat(snapshot).isNull();

        persistenceContext.getDatabaseSnapshot(PERSON_JACK_ID, PERSON_JACK);
        Person cachedSnapshot = (Person) persistenceContext.getCachedDatabaseSnapshot(PERSON_JACK_ID);

        assertAll("캐싱된 데이터베이스 스냅샷 조회", () -> {
            assertThat(cachedSnapshot.getId()).isEqualTo(PERSON_JACK_ID);
            assertThat(cachedSnapshot.getName()).isEqualTo(PERSON_JACK.getName());
            assertThat(cachedSnapshot.getAge()).isEqualTo(PERSON_JACK.getAge());
            assertThat(cachedSnapshot.getEmail()).isEqualTo(PERSON_JACK.getEmail());
            assertThat(cachedSnapshot.getIndex()).isNull();
        });
    }
}
