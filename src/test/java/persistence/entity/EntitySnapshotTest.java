package persistence.entity;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.SimpleEntityFixture;

class EntitySnapshotTest {

    @Test
    @DisplayName("[성공] Snapshot 과 Entity 의 차이 여부를 확인한다.")
    void hasDifferenceWith() {
        SimpleEntityFixture entity = new SimpleEntityFixture(100, 100L, null);
        EntitySnapshot snapshot = new EntitySnapshot(entity);

        entity.setField1(9999);
        entity.setField3("hellonayeon");

        assertTrue(snapshot.hasDifferenceWith(entity));
    }

}
