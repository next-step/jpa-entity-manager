package persistence.entity;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EntitySnapshotTest {

    @Test
    @DisplayName("[성공] Snapshot 과 Entity 의 차이 여부를 확인한다.")
    void hasDifferenceWith() {
        EntityFixture entity = new EntityFixture(100, 100L, null);
        EntitySnapshot snapshot = new EntitySnapshot(entity);

        entity.setField1(9999);
        entity.setField3("hellonayeon");

        assertTrue(snapshot.hasDifferenceWith(entity));
    }

    private static class EntityFixture {

        private int field1;
        private long field2;
        private String field3;

        public EntityFixture() {

        }

        public EntityFixture(int field1, long field2, String field3) {
            this.field1 = field1;
            this.field2 = field2;
            this.field3 = field3;
        }

        public void setField1(int field1) {
            this.field1 = field1;
        }

        public void setField2(long field2) {
            this.field2 = field2;
        }

        public void setField3(String field3) {
            this.field3 = field3;
        }

    }

}
