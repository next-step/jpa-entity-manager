package persistence.entity.context.cache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.EntityMetaDataTestSupport;
import persistence.PersonV3FixtureFactory;
import persistence.sql.ddl.PersonV3;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EntitySnapshotsTest extends EntityMetaDataTestSupport {

    private final EntitySnapshots snapshots = new EntitySnapshots();

    @DisplayName("엔티티 객체를 저장된 스냅샷과 비교한다")
    @Test
    public void compareSnapshot() throws Exception {
        // given
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub();
        final EntityKey<PersonV3> entityKey = new EntityKey<>(person.getId(), person.getClass().getName());
        snapshots.add(entityKey, person);

        // when
        final boolean isSame = snapshots.compareWithSnapshot(entityKey, person);

        // then
        assertTrue(isSame);
    }

}
