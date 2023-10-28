package persistence.entity;

import domain.FixturePerson;
import domain.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class EntitySnapshotsTest {
    private EntitySnapshots entitySnapshots;
    private Person fixture;
    private EntityKey fixtureEntityKey;

    @BeforeEach
    void setUp() {
        entitySnapshots = new EntitySnapshots();
        fixture = FixturePerson.create(1L);
        fixtureEntityKey = new EntityKey(Person.class, 1L);
    }

    @Test
    @DisplayName("Entity 를 처음 넣는다면 동등하지만 동일하지 않은 객체를 반환한다.")
    void firstGetDatabaseSnapshotTest() {
        final Object databaseSnapshot = entitySnapshots.getDatabaseSnapshot(fixtureEntityKey, fixture);

        assertSoftly(softly -> {
            softly.assertThat(databaseSnapshot == fixture).isFalse();
            softly.assertThat(databaseSnapshot.getClass()).isEqualTo(fixture.getClass());
            final Person castedDatabaseSnapshot = fixture.getClass().cast(databaseSnapshot);
            softly.assertThat(castedDatabaseSnapshot.getId()).isEqualTo(fixture.getId());
            softly.assertThat(castedDatabaseSnapshot.getName()).isEqualTo(fixture.getName());
            softly.assertThat(castedDatabaseSnapshot.getAge()).isEqualTo(fixture.getAge());
            softly.assertThat(castedDatabaseSnapshot.getEmail()).isEqualTo(fixture.getEmail());
        });
    }

    @Test
    @DisplayName("두번째 getDatabaseSnapshot 부터는 첫 getDatabaseSnapshot 과 동일한 객체를 반환한다.")
    void afterFirstGetDatabaseSnapshotTest() {
        final Object databaseSnapshot = entitySnapshots.getDatabaseSnapshot(fixtureEntityKey, fixture);
        final Object databaseSnapshotV2 = entitySnapshots.getDatabaseSnapshot(fixtureEntityKey, fixture);

        assertThat(databaseSnapshot == databaseSnapshotV2).isTrue();
    }
}
