package persistence.entity.context.cache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.PersonV3FixtureFactory;
import persistence.sql.ddl.PersonV3;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceCacheTest {

    private final PersistenceCache persistenceCache = new PersistenceCache();

    @DisplayName("영속성 캐시에 엔티티를 저장한다")
    @Test
    public void add() throws Exception {
        // given
        final PersonV3 entity = PersonV3FixtureFactory.generatePersonV3Stub();
        final EntityKey entityKey = new EntityKey(entity.getId(), entity.getClass().getName());

        // when
        persistenceCache.add(entityKey, entity);

        // then
        final PersonV3 cachedEntity = persistenceCache.get(entityKey);
        assertEquals(entity, cachedEntity);
    }

    @DisplayName("영속성 캐시에 저장된 엔티티를 가져온다")
    @Test
    public void get() throws Exception {
        // given
        final PersonV3 entity = PersonV3FixtureFactory.generatePersonV3Stub();
        final EntityKey entityKey = new EntityKey(entity.getId(), entity.getClass().getName());
        persistenceCache.add(entityKey, entity);

        // when
        final PersonV3 cachedEntity = persistenceCache.get(entityKey);

        // then
        assertEquals(entity, cachedEntity);
    }

    @DisplayName("영속성 캐시에 저장된 엔티티를 삭제한다")
    @Test
    public void remove() throws Exception {
        // given
        final PersonV3 entity = PersonV3FixtureFactory.generatePersonV3Stub();
        final EntityKey entityKey = new EntityKey(entity.getId(), entity.getClass().getName());
        persistenceCache.add(entityKey, entity);

        // when
        persistenceCache.remove(entityKey);

        // then
        final PersonV3 cachedEntity = persistenceCache.get(entityKey);
        assertNull(cachedEntity);
    }

}
