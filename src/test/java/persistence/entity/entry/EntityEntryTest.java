package persistence.entity.entry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EntityEntryTest {
    @Test
    @DisplayName("정해진 라이프사이클대로 상태를 변경할 수 있다.")
    void setStatusSuccess() {
        EntityEntry entityEntry = new EntityEntry(EntityEntryStatus.LOADING);
        entityEntry.setStatus(EntityEntryStatus.MANAGED);
        entityEntry.setStatus(EntityEntryStatus.DELETED);
        entityEntry.setStatus(EntityEntryStatus.GONE);

        assertEquals(EntityEntryStatus.GONE, entityEntry.getStatus());
    }

    @Test
    @DisplayName("정해진 라이프사이클을 어긋나 상태를 바꾸려 하면 에러를 내뱉는다.")
    void setStatusFail() {
        EntityEntry entityEntry = new EntityEntry(EntityEntryStatus.SAVING);

        assertThrows(IllegalStateException.class, () -> entityEntry.setStatus(EntityEntryStatus.DELETED));
    }

    @Test
    @DisplayName("업데이트 가능한 상태인지 판별한다.")
    void testIsUpdatable() {
        assertAll(
                () -> assertTrue(new EntityEntry(EntityEntryStatus.MANAGED).isUpdatable()),
                () -> assertFalse(new EntityEntry(EntityEntryStatus.SAVING).isUpdatable()),
                () -> assertFalse(new EntityEntry(EntityEntryStatus.LOADING).isUpdatable()),
                () -> assertFalse(new EntityEntry(EntityEntryStatus.READ_ONLY).isUpdatable()),
                () -> assertFalse(new EntityEntry(EntityEntryStatus.DELETED).isUpdatable()),
                () -> assertFalse(new EntityEntry(EntityEntryStatus.GONE).isUpdatable())
        );
    }

    @Test
    @DisplayName("삭제 가능한 상태인지 판별한다.")
    void testIsDeletable() {
        assertAll(
                () -> assertTrue(new EntityEntry(EntityEntryStatus.MANAGED).isDeletable()),
                () -> assertFalse(new EntityEntry(EntityEntryStatus.SAVING).isDeletable()),
                () -> assertFalse(new EntityEntry(EntityEntryStatus.LOADING).isDeletable()),
                () -> assertFalse(new EntityEntry(EntityEntryStatus.READ_ONLY).isDeletable()),
                () -> assertFalse(new EntityEntry(EntityEntryStatus.DELETED).isDeletable()),
                () -> assertFalse(new EntityEntry(EntityEntryStatus.GONE).isDeletable())
        );
    }
}
