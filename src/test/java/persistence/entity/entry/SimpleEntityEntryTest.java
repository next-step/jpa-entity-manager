package persistence.entity.entry;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@Nested
@DisplayName("SimpleEntityEntry 클래스의")
class SimpleEntityEntryTest {
    @Nested
    @DisplayName("updateStatus 메소드는")
    class updateStatus {
        @Test
        @DisplayName("상태를 변경한다.")
        void notThrow() {
            EntityEntry simpleEntityEntry = new SimpleEntityEntry(Status.MANAGED);

            simpleEntityEntry.updateStatus(Status.GONE);
            EntityEntry target = new SimpleEntityEntry(Status.GONE);

            assertThat(simpleEntityEntry).isEqualTo(target);
        }
    }
}
