package persistence.entity;

import domain.FixturePerson;
import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityEntryTest {

    @Test
    @DisplayName("person instance 를 이용해 EntityEntry 객체를 생성할 수 있다.")
    void entityEntryCreateTest() {
        final Person person = FixturePerson.create(null);
        final EntityEntry entityEntry = new EntityEntry(person, Status.LOADING);

        assertSoftly(softly -> {
            softly.assertThat(entityEntry).isNotNull();
            softly.assertThat(entityEntry.getStatus()).isEqualTo(Status.LOADING);
        });
    }

    @Test
    @DisplayName("person instance 를 이용해 EntityEntry 의 status 를 변경할 수 있다.")
    void entityEntryUpdateStatusTest() {
        final Person person = FixturePerson.create(null);
        final EntityEntry entityEntry = new EntityEntry(person, Status.LOADING);
        entityEntry.updateStatus(Status.MANAGED);

        assertSoftly(softly -> {
            softly.assertThat(entityEntry).isNotNull();
            softly.assertThat(entityEntry.getStatus()).isEqualTo(Status.MANAGED);
        });
    }
}
