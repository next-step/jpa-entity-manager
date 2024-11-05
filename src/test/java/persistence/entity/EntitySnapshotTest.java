package persistence.entity;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EntitySnapshotTest {

    @Test
    @DisplayName("[성공] Snapshot 과 Entity 를 비교했을 때 변경된 필드만 설정된다.")
    void compare() {
        EntityFixture entity = new EntityFixture(100, 100L, null);
        EntitySnapshot snapshot = new EntitySnapshot(entity);

        entity.setField1(9999);
        entity.setField3("hellonayeon");

        Object diffObject = snapshot.compare(entity);
        Assertions.assertAll("변경된 필드값 검증",
                () -> assertEquals(getField("field1", diffObject), 9999),
                () -> assertEquals(getField("field2", diffObject), 0L),
                () -> assertEquals(getField("field3", diffObject), "hellonayeon"));
    }

    private Object getField(String fieldName, Object diffObject) throws IllegalAccessException, NoSuchFieldException {
        Field field = getField(EntityFixture.class, fieldName);
        field.setAccessible(true);
        return field.get(diffObject);
    }

    @NotNull
    private Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        return clazz.getDeclaredField(fieldName);
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
