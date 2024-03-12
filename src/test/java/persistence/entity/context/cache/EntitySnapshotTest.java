package persistence.entity.context.cache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.EntityMetaDataTestSupport;
import persistence.PersonV3FixtureFactory;
import persistence.sql.ddl.PersonV3;

import static org.assertj.core.api.Assertions.assertThat;

class EntitySnapshotTest extends EntityMetaDataTestSupport {

    @DisplayName("외부 객체와 동일한 필드 값을 가지고 있는지 확인한다")
    @Test
    public void isSame() throws Exception {
        // given
        final PersonV3 person = PersonV3FixtureFactory.generatePersonV3Stub();
        final EntitySnapshot snapshot = new EntitySnapshot(person);

        // when
        final boolean same = snapshot.isSame(person);

        // then
        assertThat(same).isTrue();
    }

}
