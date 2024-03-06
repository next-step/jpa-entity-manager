package persistence.entity.data;

import database.sql.Person;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class EntitySnapshotTest {

    @Test
    void getValueWithNullValues() {
        Person person = new Person();
        EntitySnapshot oldEntitySnapshot = EntitySnapshot.of(null);
        EntitySnapshot entitySnapshot = EntitySnapshot.of(person);

        Map<String, Object> changes = oldEntitySnapshot.diff(entitySnapshot);

        assertAll(
                () -> assertThat(changes.get("nick_name")).isNull(),
                () -> assertThat(changes.get("old")).isNull(),
                () -> assertThat(changes.get("email")).isNull()
        );
    }

    private final Person person0 = new Person();
    private final Person person1 = new Person(1L, "이름", 10, "이메일@a.com");
    private final Person person2 = new Person(1L, "이름2", 20, "이메일@a.com");

    @Test
    void changes() {
        assertAll(
                () -> assertThat(changes(person0, person0)).isEqualTo(Map.of()),
                () -> assertThat(changes(person0, person1)).isEqualTo(Map.of("nick_name", "이름", "old", 10, "email", "이메일@a.com")),
                () -> assertThat(changes(person0, person2)).isEqualTo(Map.of("nick_name", "이름2", "old", 20, "email", "이메일@a.com")),
                () -> {
                    Map<String, Object> map = new HashMap<>() {{
                        put("nick_name", null);
                        put("old", null);
                        put("email", null);
                    }};
                    assertThat(changes(person1, person0)).isEqualTo(map);
                },
                () -> assertThat(changes(person1, person1)).isEqualTo(Map.of()),
                () -> assertThat(changes(person1, person2)).isEqualTo(Map.of("nick_name", "이름2", "old", 20)),
                () -> {
                    Map<String, Object> map = new HashMap<>() {{
                        put("nick_name", null);
                        put("old", null);
                        put("email", null);
                    }};
                    assertThat(changes(person2, person0)).isEqualTo(map);
                },
                () -> assertThat(changes(person2, person1)).isEqualTo(Map.of("nick_name", "이름", "old", 10)),
                () -> assertThat(changes(person2, person2)).isEqualTo(Map.of())

        );
    }

    private Map<String, Object> changes(Person p1, Person p2) {
        return EntitySnapshot.of(p1).diff(EntitySnapshot.of(p2));
    }

}
