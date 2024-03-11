package persistence.entity.context;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.PersonV3FixtureFactory;
import persistence.sql.ddl.PersonV3;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceContextTest {

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
        assertEquals(person, cachedEntity);
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
        assertEquals(person, cachedEntity);
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
        assertNull(cachedEntity);
    }

}
