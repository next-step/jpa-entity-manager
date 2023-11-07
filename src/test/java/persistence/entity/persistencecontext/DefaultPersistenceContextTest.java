package persistence.entity.persistencecontext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.Person;
import persistence.entity.persistencecontext.DefaultPersistenceContext;
import persistence.entity.persistencecontext.EntityPersistIdentity;
import persistence.entity.persistencecontext.EntitySnapShot;
import persistence.testutils.ReflectionTestSupport;

import static org.assertj.core.api.Assertions.*;
import static persistence.entity.PersonFixtures.fixtureById;

class DefaultPersistenceContextTest {
    private DefaultPersistenceContext<Person> context;

    @BeforeEach
    void setUp() {
        context = new DefaultPersistenceContext<>();
    }

    @DisplayName("PersistenceContext에 Entity를 추가하고, 동일한 Entity를 조회하면 같은 Entity를 반환한다.")
    @Test
    void add() {
        // given
        Person person = fixtureById(1L);

        EntityPersistIdentity id = new EntityPersistIdentity(Person.class, 1L);

        // when
        context.addEntity(id, person);

        // then
        assertThat(context.getEntity(id)).isEqualTo(person);
    }

    @DisplayName("PersistenceContext에 Entity가 저장되어 있지 않으면 null을 반환한다.")
    @Test
    void getEntity() {
        // given
        EntityPersistIdentity id = new EntityPersistIdentity(Person.class, 1L);

        // when then
        assertThat(context.getEntity(id)).isNull();
    }

    @DisplayName("PersistenceContext에 Entity를 추가하고, 동일한 Entity를 삭제하면 PersistenceContext에서 Entity를 제거한다.")
    @Test
    void removeEntity() {
        // given
        Person person = fixtureById(1L);
        Person person2 = fixtureById(2L);
        context.addEntity(new EntityPersistIdentity(Person.class, 1L), person);

        // when
        context.removeEntity(person);

        // then
        assertThat(context.getEntity(new EntityPersistIdentity(Person.class, 1L))).isNull();
    }

    @DisplayName("PersistenceContext에 Entity를 추가하고, 동일하지 않은 Entity를 삭제하면 PersistenceContext에서 Entity를 제거하지 않는다.")
    @Test
    void removeEntity_not_remove() {
        // given
        Person person = fixtureById(1L);
        Person person2 = fixtureById(2L);
        context.addEntity(new EntityPersistIdentity(Person.class, 1L), person);
        context.addEntity(new EntityPersistIdentity(Person.class, 2L), person2);

        // when
        context.removeEntity(person2);

        // then
        assertThat(context.getEntity(new EntityPersistIdentity(Person.class, 1L))).isNotNull();
    }

    @DisplayName("최초 snapshot 조회시 존재하지 않음")
    @Test
    void getDatabaseSnapshot_null() {
        EntitySnapShot snapShot = (EntitySnapShot) context.getDatabaseSnapshot(new EntityPersistIdentity(Person.class, 1L), fixtureById(1L));

        assertThat(snapShot).isNull();
    }

    @DisplayName("최초 이후의 snapshot 조회시 존재")
    @Test
    void getDatabaseSnapshot() {
        long id = 1L;
        Person person = fixtureById(id);
        EntityPersistIdentity entityPersistId = new EntityPersistIdentity(Person.class, id);

        EntitySnapShot snapShot = (EntitySnapShot) context.getDatabaseSnapshot(entityPersistId, person);
        assertThat(snapShot).isNull();

        ReflectionTestSupport.setFieldValue(person, "name", "test");

        EntitySnapShot nameChangedSnapShot = (EntitySnapShot) context.getDatabaseSnapshot(entityPersistId, person);
        assertThat(nameChangedSnapShot).isNotNull();
    }
}
