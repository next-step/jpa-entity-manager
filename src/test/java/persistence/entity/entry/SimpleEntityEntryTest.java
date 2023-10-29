package persistence.entity.entry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@Nested
@DisplayName("SimpleEntityEntry 클래스의")
class SimpleEntityEntryTest {
    @Nested
    @DisplayName("updateStatus 메소드는")
    class updateStatus {
        @Test
        @DisplayName("상태를 변경한다.")
        void notThrow() {
            EntityEntry simpleEntityEntry = new SimpleEntityEntry(Status.PERSISTENT);
            Assertions.assertDoesNotThrow(() -> simpleEntityEntry.updateStatus(Status.REMOVED));
        }
    }
}
