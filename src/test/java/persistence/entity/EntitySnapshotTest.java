package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.PersonWithTransientAnnotation;

import static org.junit.jupiter.api.Assertions.*;

public class EntitySnapshotTest {
    final PersonWithTransientAnnotation entity = new PersonWithTransientAnnotation(
            1L, "홍길동", 20, "test@test.com", 1
    );

    @Test
    @DisplayName("update가 호출되면 엔티티에 더티체킹 처리한다.")
    void testUpdate() {
        // given
        EntitySnapshot entitySnapshot = new EntitySnapshot(entity);

        // when
        entity.setAge(30);
        entitySnapshot.update(entity);

        // then
        assertTrue(entitySnapshot.isDirty());
    }

    @Test
    @DisplayName("기존 엔티티에 변경사항이 없다면 더티체킹 처리하지 않는다.")
    void testUpdateNothing() {
        // given
        EntitySnapshot entitySnapshot = new EntitySnapshot(entity);

        // when
        entitySnapshot.update(entity);

        // then
        assertFalse(entitySnapshot.isDirty());
    }
}
