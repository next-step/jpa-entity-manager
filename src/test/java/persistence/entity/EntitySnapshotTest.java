package persistence.entity;

import database.sql.Person;
import org.junit.jupiter.api.Test;
import persistence.entity.data.EntitySnapshot;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntitySnapshotTest {

    @Test
    void getValueWithNullValues() {
        Person person = new Person();
        EntitySnapshot entitySnapshot = new EntitySnapshot(person);

        assertAll(
                () -> assertThat(entitySnapshot.getValue("nick_name")).isNull(),
                () -> assertThat(entitySnapshot.getValue("old")).isNull(),
                () -> assertThat(entitySnapshot.getValue("email")).isNull()
        );
    }

    @Test
    void getValue() {
        Person person = new Person(1L, "namename", 111, "e@xamp.le");
        EntitySnapshot entitySnapshot = new EntitySnapshot(person);

        assertAll(
                () -> assertThat(entitySnapshot.getValue("nick_name")).isEqualTo("namename"),
                () -> assertThat(entitySnapshot.getValue("old")).isEqualTo(111),
                () -> assertThat(entitySnapshot.getValue("email")).isEqualTo("e@xamp.le")
        );
    }

    @Test
    void keys() {
        Person person = new Person();
        EntitySnapshot entitySnapshot = new EntitySnapshot(person);

        assertThat(entitySnapshot.keys()).containsAll(List.of("nick_name", "old", "email"));
    }
}
