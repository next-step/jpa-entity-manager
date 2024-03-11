package persistence.sql.entity.context;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class PersistenceContextTest {

    private PersistenceContext persistenceContext;
    private Person person;

    @BeforeEach
    void setUp() {
        this.person = new Person(1L, "박재성", 10, "jason");
        this.persistenceContext = new PersistenceContextImpl();

        persistenceContext.addEntity(person, person.getId());
    }

    @DisplayName("1차 캐시에 저장된 값 가져온다.")
    @Test
    void getEntityTest() {
        Person entity = persistenceContext.getEntity(Person.class, 1L);

        assertThat(entity).isEqualTo(person);
    }

    @DisplayName("1차 캐시에 저장된 값이 없을 경우 널을 반환한다.")
    @Test
    void isExistsEntityTest() {
        Person entity = persistenceContext.getEntity(Person.class, 2L);

        assertThat(entity).isNull();
    }

    @DisplayName("1차 캐시와 스냅샷에도 저장이 된다.")
    @Test
    void addEntityTest() {
        Person person2 = new Person(2L, "김성주", 12, "jpa@nextstep.com");
        persistenceContext.addEntity(person2, person2.getId());

        Person cacheEntity = persistenceContext.getEntity(Person.class, 2L);
        Person snapshotEntity = persistenceContext.getDatabaseSnapshot(Person.class, 2L);

        assertAll(
                () -> assertThat(cacheEntity).isEqualTo(person2),
                () -> assertThat(snapshotEntity).isEqualTo(person2)
        );
    }

    @DisplayName("1차캐시와 스냅샷 데이터가 다르면 업데이트 된다.")
    @Test
    void updateEntityTest() {
        Person updatePerson = new Person(person.getId(), "이동규", 13, "cu@nextstep.com");

        persistenceContext.addEntity(updatePerson, updatePerson.getId());

        Person entity = persistenceContext.getEntity(Person.class, person.getId());

        assertThat(person).isNotEqualTo(entity);
    }

    @DisplayName("1차캐시와 스냅샷 데이터를 삭제한다.")
    @Test
    void removeEntityTest() {
        persistenceContext.removeEntity(person);
        persistenceContext.goneEntity(person);

        boolean isGone = persistenceContext.isGone(person);

        assertThat(isGone).isTrue();
    }

    @DisplayName("캐시와 스냅샷 데이터를 삭제한다.")
    @Test
    void removeAllTest() {
        persistenceContext.removeAll();

        Person cacheEntity = persistenceContext.getEntity(Person.class, person.getId());
        Person snapshotEntity = persistenceContext.getDatabaseSnapshot(Person.class, person.getId());

        assertAll(
                () -> assertThat(cacheEntity).isNull(),
                () -> assertThat(snapshotEntity).isNull()
        );
    }

}
