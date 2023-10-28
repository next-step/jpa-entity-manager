package persistence.entity;

import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntityEntriesTest {
    private EntityEntries entityEntries;
    private EntityKey entityKey;

    @BeforeEach
    void setUp() {
        entityEntries = new EntityEntries();
        entityKey = new EntityKey(Person.class, 1L);
    }

    @Test
    @DisplayName("EntityEntries 를 통해 EntityEntry 들를 저장하고 조회할 수 있다.")
    void entityEntriesTest() {
        entityEntries.addEntityEntry(entityKey, Status.LOADING);

        final Optional<EntityEntry> result = entityEntries.getEntityEntry(entityKey);

        assertSoftly(softly -> {
            softly.assertThat(result).isNotEmpty();
            softly.assertThat(result.get().getStatus()).isEqualTo(Status.LOADING);
        });
    }

    @Test
    @DisplayName("EntityEntries 를 통해 없는 key 조회시 Optional.empty 가 반환된다.")
    void getEntityEntryNotExistTest() {
        final Optional<EntityEntry> result = entityEntries.getEntityEntry(entityKey);

        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("updateEntityEntryStatus 를 통해 해당 key 의 EntityEntry 의 상태를 변경할 수 있다.")
    void updateEntityEntryStatusTest() {
        entityEntries.addEntityEntry(entityKey, Status.LOADING);

        entityEntries.updateEntityEntryStatus(entityKey, Status.MANAGED);

        final Optional<EntityEntry> result = entityEntries.getEntityEntry(entityKey);

        assertSoftly(softly -> {
            softly.assertThat(result).isNotEmpty();
            softly.assertThat(result.get().getStatus()).isEqualTo(Status.MANAGED);
        });
    }


}
