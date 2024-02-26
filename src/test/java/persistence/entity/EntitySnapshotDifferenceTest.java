package persistence.entity;

import database.sql.Person;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntitySnapshotDifferenceTest {
    private Person person0 = new Person();
    private Person person1 = new Person(1L, "이름", 10, "이메일@a.com");
    private Person person2 = new Person(1L, "이름2", 20, "이메일@a.com");

    @Test
    void isDirty() {
        assertAll(
                () -> assertThat(buildDifference(person0, person1).isDirty()).isTrue(),
                () -> assertThat(buildDifference(person0, person2).isDirty()).isTrue(),
                () -> assertThat(buildDifference(person1, person2).isDirty()).isTrue(),
                () -> assertThat(buildDifference(person0, person0).isDirty()).isFalse(),
                () -> assertThat(buildDifference(person1, person1).isDirty()).isFalse(),
                () -> assertThat(buildDifference(person2, person2).isDirty()).isFalse()
        );
    }

    @Test
    void toMap() {
        assertAll(
                () -> assertThat(buildDifference(person0, person1).toMap()).isEqualTo(Map.of("nick_name", "이름", "old", 10, "email", "이메일@a.com")),
                () -> assertThat(buildDifference(person0, person2).toMap()).isEqualTo(Map.of("nick_name", "이름2", "old", 20, "email", "이메일@a.com")),
                () -> assertThat(buildDifference(person1, person2).toMap()).isEqualTo(Map.of("nick_name", "이름2", "old", 20)),
                () -> assertThat(buildDifference(person0, person0).toMap()).isEqualTo(Map.of()),
                () -> assertThat(buildDifference(person1, person1).toMap()).isEqualTo(Map.of()),
                () -> assertThat(buildDifference(person2, person2).toMap()).isEqualTo(Map.of())
        );
    }

    private EntitySnapshotDifference buildDifference(Person p1, Person p2) {
        return new EntitySnapshotDifference(
                new EntitySnapshot(p1),
                new EntitySnapshot(p2));
    }
}
