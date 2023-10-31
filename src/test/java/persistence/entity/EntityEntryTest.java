package persistence.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityEntryTest {

    @Test
    @DisplayName("EntityEntry 객체를 생성할 수 있다.")
    void entityEntryCreateTest() {
        final EntityEntry entityEntry = new EntityEntry(Status.LOADING);

        assertSoftly(softly -> {
            softly.assertThat(entityEntry).isNotNull();
            softly.assertThat(entityEntry.getStatus()).isEqualTo(Status.LOADING);
        });
    }

    @Test
    @DisplayName("EntityEntry 의 status 를 변경할 수 있다.")
    void entityEntryUpdateStatusTest() {
        final EntityEntry entityEntry = new EntityEntry(Status.LOADING);
        entityEntry.updateStatus(Status.MANAGED);

        assertSoftly(softly -> {
            softly.assertThat(entityEntry).isNotNull();
            softly.assertThat(entityEntry.getStatus()).isEqualTo(Status.MANAGED);
        });
    }
}
