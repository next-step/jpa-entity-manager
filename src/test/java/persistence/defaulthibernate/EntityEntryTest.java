package persistence.defaulthibernate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EntityEntryTest {

    @Test
    @DisplayName("EntityEntry 생성 시 상태가 null이면 예외가 발생한다")
    void createEntityEntry_WithNullStatus_ThrowsException() {
        // Given
        TestEntity testEntity = new TestEntity(1L, "test");

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> new EntityEntry(null, testEntity, 1L, new Object[]{"test"}));
    }

    @Test
    @DisplayName("EntityEntry 생성이 정상적으로 된다")
    void createEntityEntry_Success() {
        // Given
        TestEntity testEntity = new TestEntity(1L, "test");
        Object[] loadedState = new Object[]{1L, "test"};

        // When
        EntityEntry entityEntry = new EntityEntry(EntryStatus.MANAGED, testEntity, 1L, loadedState);

        // Then
        assertEquals(EntryStatus.MANAGED, entityEntry.getStatus());
        assertEquals(1L, entityEntry.getId());
        assertEquals(TestEntity.class, entityEntry.getEntityClass());
        assertArrayEquals(loadedState, entityEntry.getLoadedState());
    }

    @Test
    @DisplayName("EntityEntry의 상태를 변경할 수 있다")
    void setStatus_Success() {
        // Given
        TestEntity testEntity = new TestEntity(1L, "test");
        EntityEntry entityEntry = new EntityEntry(
                EntryStatus.MANAGED,
                testEntity,
                1L,
                new Object[]{1L, "test"}
        );

        // When
        entityEntry.setStatus(EntryStatus.DELETED);

        // Then
        assertEquals(EntryStatus.DELETED, entityEntry.getStatus());
    }

    @Test
    @DisplayName("EntityEntry의 상태를 null로 변경하면 예외가 발생한다")
    void setStatus_WithNullStatus_ThrowsException() {
        // Given
        TestEntity testEntity = new TestEntity(1L, "test");
        EntityEntry entityEntry = new EntityEntry(
                EntryStatus.MANAGED,
                testEntity,
                1L,
                new Object[]{1L, "test"}
        );

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> entityEntry.setStatus(null));
    }

    @Test
    @DisplayName("EntityEntry의 스냅샷 데이터를 조회할 수 있다")
    void getLoadedState_ReturnsCopy() {
        // Given
        TestEntity testEntity = new TestEntity(1L, "test");
        Object[] originalState = new Object[]{1L, "test"};
        EntityEntry entityEntry = new EntityEntry(
                EntryStatus.MANAGED,
                testEntity,
                1L,
                originalState
        );

        // When
        Object[] loadedState = entityEntry.getLoadedState();
        // Then
        assertArrayEquals(originalState, loadedState);
        // 반환된 배열이 원본과 다른 인스턴스인지 확인
        assertNotSame(originalState, loadedState);
    }

    @Test
    @DisplayName("동일한 엔티티의 스냅샷은 같은 상태를 가진다")
    void compareLoadedStates_WithSameEntity_AreEqual() {
        // Given
        TestEntity testEntity = new TestEntity(1L, "test");
        Object[] state1 = new Object[]{1L, "test"};
        Object[] state2 = new Object[]{1L, "test"};

        EntityEntry entry1 = new EntityEntry(EntryStatus.MANAGED, testEntity, 1L, state1);
        EntityEntry entry2 = new EntityEntry(EntryStatus.MANAGED, testEntity, 1L, state2);

        // When & Then
        assertArrayEquals(entry1.getLoadedState(), entry2.getLoadedState());
    }

    // 테스트용 엔티티 클래스
    private static class TestEntity {
        private Long id;
        private String name;

        public TestEntity(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }
}