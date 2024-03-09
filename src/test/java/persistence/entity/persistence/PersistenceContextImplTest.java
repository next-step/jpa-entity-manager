package persistence.entity.persistence;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.domain.EntitySnapshot;
import persistence.sql.dml.entity.Person;

import static org.assertj.core.api.Assertions.assertThat;

class PersistenceContextImplTest {

    @Test
    @DisplayName("PersistenceContextImpl 객체를 이용한 이용한 추가 테스트")
    void addEntityTest() {
        // given
        PersistenceContext persistenceContext = new PersistenceContextImpl();
        Person person = new Person(1L, "jay", 32, "jay@mail.com");

        // when
        persistenceContext.addEntity(1L, person);

        // then
        assertThat(persistenceContext.getEntity(Person.class, 1L)).isNotNull();
    }

    @Test
    @DisplayName("PersistenceContextImpl 객체를 이용한 이용한 조회 테스트")
    void getEntityTest() {
        // given
        PersistenceContext persistenceContext = new PersistenceContextImpl();
        Person expectedPerson = new Person(1L, "jay", 32, "jay@mail.com");
        persistenceContext.addEntity(1L, expectedPerson);

        // when
        Person actualPerson = persistenceContext.getEntity(Person.class, 1L);

        // then
        assertThat(actualPerson).isEqualTo(expectedPerson);
    }

    @Test
    @DisplayName("PersistenceContextImpl 객체를 이용한 이용한 삭제 테스트")
    void removeEntityTest() {
        // given
        PersistenceContext persistenceContext = new PersistenceContextImpl();
        Person person = new Person(1L, "jay", 32, "jay@mail.com");
        persistenceContext.addEntity(1L, person);

        // when
        persistenceContext.removeEntity(person);

        // then
        assertThat(persistenceContext.getEntity(Person.class, 1L)).isNull();
    }

    @Test
    void getDatabaseSnapshotTest() {
        // given
        PersistenceContext persistenceContext = new PersistenceContextImpl();
        Person person = new Person(1L, "jay", 32, "jay@mail.com");

        // when
        EntitySnapshot snapshot = persistenceContext.getDatabaseSnapshot(1L, person);

        // then
        assertThat(snapshot.getSnapshot()).isNotZero();
    }

    @Test
    void getCachedDatabaseSnapshotTest() {
        // given
        PersistenceContext persistenceContext = new PersistenceContextImpl();
        Person person = new Person(1L, "jay", 32, "jay@mail.com");
        EntitySnapshot snapshot = persistenceContext.getDatabaseSnapshot(1L, person);

        // when
        EntitySnapshot cachedSnapshot = persistenceContext.getCachedDatabaseSnapshot(1L, person);

        // then
        assertThat(cachedSnapshot).isEqualTo(snapshot);
    }
}
