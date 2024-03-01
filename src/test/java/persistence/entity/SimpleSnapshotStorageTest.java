package persistence.entity;

import org.junit.jupiter.api.Test;
import persistence.sql.entity.Person;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class SimpleSnapshotStorageTest {

    private final SnapshotStorage snapshotStorage = new SimpleSnapshotStorage();

    @Test
    void should_do_dirty_check() {
        Long id = 1L;
        String name = "cs";
        Integer age = 29;
        String email = "katd216@gmail.com";
        Person person = new Person(id, name, age, email, 1);

        snapshotStorage.add(person);

        assertAll(
                () -> assertThat(snapshotStorage.isDirty(person)).isFalse(),
                () -> assertThat(snapshotStorage.isDirty(new Person(id, name, age, email, 1))).isFalse(),
                () -> assertThat(snapshotStorage.isDirty(new Person(id, "newName", 32, "newEmail@gmail.com", 1))).isTrue()
        );


    }
}
