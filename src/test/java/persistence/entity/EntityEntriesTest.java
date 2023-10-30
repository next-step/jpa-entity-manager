package persistence.entity;

import domain.FixturePerson;
import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.exception.PersistenceException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityEntriesTest {
    private EntityEntries entityEntries;
    private Person person;

    @BeforeEach
    void setUp() {
        entityEntries = new EntityEntries();
        person = FixturePerson.create(null);
    }

    @Test
    @DisplayName("EntityEntries 를 통해 EntityEntry 들을 person instance 를 이용해 저장하고 조회할 수 있다.")
    void entityEntriesTest() {
        entityEntries.addEntityEntry(person, Status.LOADING);

        final Optional<EntityEntry> result = entityEntries.getEntityEntry(person);

        assertSoftly(softly -> {
            softly.assertThat(result).isNotEmpty();
            softly.assertThat(result.get().getStatus()).isEqualTo(Status.LOADING);
        });
    }

    @Test
    @DisplayName("EntityEntries 를 통해 없는 person instance 조회시 Optional.empty 가 반환된다.")
    void getEntityEntryNotExistTest() {
        final Person newPerson = FixturePerson.create(null);
        final Optional<EntityEntry> result = entityEntries.getEntityEntry(newPerson);

        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("updateEntityEntryStatus 를 통해 person instance 의 EntityEntry 의 상태를 변경할 수 있다.")
    void updateEntityEntryStatusTest() {
        entityEntries.addEntityEntry(person, Status.LOADING);

        entityEntries.updateEntityEntryStatus(person, Status.MANAGED);
        final Optional<EntityEntry> result = entityEntries.getEntityEntry(person);

        assertSoftly(softly -> {
            softly.assertThat(result).isNotEmpty();
            softly.assertThat(result.get().getStatus()).isEqualTo(Status.MANAGED);
        });
    }

    @Test
    @DisplayName("updateEntityEntryStatus 를 통해 property 가 변한 person instance 의 EntityEntry 의 상태를 변경할 수 있다.")
    void updateEntityEntryStatusWithChangedPropertyTest() {
        entityEntries.addEntityEntry(person, Status.LOADING);

        person.changeEmail("chagned@email.com");
        entityEntries.updateEntityEntryStatus(person, Status.MANAGED);
        final Optional<EntityEntry> result = entityEntries.getEntityEntry(person);

        assertSoftly(softly -> {
            softly.assertThat(result).isNotEmpty();
            softly.assertThat(result.get().getStatus()).isEqualTo(Status.MANAGED);
        });
    }

    @Test
    @DisplayName("동등한 person instance 지만 동일하지 않은 person instance 의 EntityEntry 는 상태를 변경할 수 없다.")
    void updateEntityEntryStatusWithDifferentPersonTest() {
        entityEntries.addEntityEntry(person, Status.LOADING);

        final Person equalPerson = FixturePerson.create(null);

        assertThatThrownBy(() ->
                entityEntries.updateEntityEntryStatus(equalPerson, Status.MANAGED)
        ).isInstanceOf(PersistenceException.class);
    }

}
