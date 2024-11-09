package persistence.entity;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.SimpleEntityFixture;

class EntitySnapshotTest {

    @Test
    @DisplayName("[성공] Snapshot 과 Entity 의 차이 여부를 확인한다.")
    void hasDifferenceWith() {
        SimpleEntityFixture entity = new SimpleEntityFixture("hellonayeon", 0);
        EntitySnapshot snapshot = new EntitySnapshot(entity);

        entity.setName("Nayeon Kwon");
        entity.setNumber(9999);

        assertTrue(snapshot.hasDifferenceWith(entity));
    }

}
