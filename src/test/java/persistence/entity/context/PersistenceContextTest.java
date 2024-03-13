package persistence.entity.context;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.EntityMetaDataTestSupport;
import persistence.PersonV3FixtureFactory;
import persistence.entity.context.cache.EntitySnapshot;
import persistence.sql.ddl.PersonV3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class PersistenceContextTest extends EntityMetaDataTestSupport {

    private final PersistenceContext persistenceContext = new StatefulPersistenceContext();

    @DisplayName("1차 캐시에 저장 된 엔티티를 가져온다")
    @Test
    public void getEntity() throws Exception {
        // given
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub();
        persistenceContext.addEntity(person.getId(), person);

        // when
        final Object cachedEntity = persistenceContext.getEntity(person.getId(), person.getClass().getName());

        // then
        assertThat(cachedEntity).isEqualTo(person);
    }

    @DisplayName("엔티티를 1차 캐시에 저장한다")
    @Test
    public void addEntity() throws Exception {
        // given
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub();

        // when
        persistenceContext.addEntity(person.getId(), person);

        // then
        final Object cachedEntity = persistenceContext.getEntity(person.getId(), person.getClass().getName());
        assertThat(cachedEntity).isSameAs(person);
    }

    @DisplayName("엔티티를 1차 캐시에서 삭제한다")
    @Test
    public void removeEntity() throws Exception {
        // given
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub();
        persistenceContext.addEntity(person.getId(), person);

        // when
        persistenceContext.removeEntity(person.getId(), person);

        // then
        final Object cachedEntity = persistenceContext.getEntity(person.getId(), person.getClass().getName());
        assertThat(cachedEntity).isNull();
    }

    @DisplayName("영속성 컨텍스트에 저장된 스냅샷을 가져온다")
    @Test
    public void getDatabaseSnapshot() throws Exception {
        // given
        final PersonV3 person1 = PersonV3FixtureFactory.generatePersonV3Stub(1L);
        final PersonV3 person2 = PersonV3FixtureFactory.generatePersonV3Stub(2L);
        persistenceContext.addEntity(person1.getId(), person1);

        // when
        final EntitySnapshot snapshot1 = persistenceContext.getDatabaseSnapshot(person1.getId(), person1);
        final EntitySnapshot snapshot2 = persistenceContext.getDatabaseSnapshot(person2.getId(), person2);

        // then
        assertAll(
                () -> assertThat(snapshot1.isSame(person1)).isTrue(),
                () -> assertThat(snapshot2.isSame(person2)).isTrue()
        );
    }

}
