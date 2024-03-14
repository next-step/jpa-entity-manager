package persistence.entity.context.cache;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.EntityMetaDataTestSupport;
import persistence.PersonV3FixtureFactory;
import persistence.sql.ddl.PersonV3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntitySnapshotTest extends EntityMetaDataTestSupport {

    @DisplayName("엔티티 객체가 더티인지 확인한다")
    @Test
    public void isSame() throws Exception {
        // given
        final PersonV3 person1 = PersonV3FixtureFactory.generatePersonV3Stub(1L);
        final PersonV3 person2 = PersonV3FixtureFactory.generatePersonV3Stub(2L);
        final EntitySnapshot snapshot1 = new EntitySnapshot(person1);
        final EntitySnapshot snapshot2 = new EntitySnapshot(person2);
        person2.setName("update name");

        // when
        final boolean dirty1 = snapshot1.checkDirty(person1);
        final boolean dirty2 = snapshot2.checkDirty(person2);

        // then
        assertAll(
                () -> assertThat(dirty1).isFalse(),
                () -> assertThat(dirty2).isTrue()
        );
    }

}
