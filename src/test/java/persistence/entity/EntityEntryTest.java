package persistence.entity;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityEntryTest {

    @Test
    @DisplayName("EntityKey 를 이용해 EntityEntry 객체를 생성할 수 있다.")
    void entityEntryCreateTest() {
        final EntityKey entityKey = new EntityKey(Person.class, 1L);
        final EntityEntry entityEntry = new EntityEntry(entityKey, Status.LOADING);

        assertSoftly(softly -> {
            softly.assertThat(entityEntry).isNotNull();
            softly.assertThat(entityEntry.getStatus()).isEqualTo(Status.LOADING);
        });
    }

    @Test
    @DisplayName("EntityKey.updateStatus 를 이용해 EntityEntry 의 status 를 변경할 수 있다..")
    void entityEntryUpdateStatusTest() {
        final EntityKey entityKey = new EntityKey(Person.class, 1L);
        final EntityEntry entityEntry = new EntityEntry(entityKey, Status.LOADING);
        entityEntry.updateStatus(Status.MANAGED);

        assertSoftly(softly -> {
            softly.assertThat(entityEntry).isNotNull();
            softly.assertThat(entityEntry.getStatus()).isEqualTo(Status.MANAGED);
        });
    }
}
