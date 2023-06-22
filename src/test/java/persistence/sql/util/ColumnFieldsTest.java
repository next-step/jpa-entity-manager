package persistence.sql.util;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnFieldsTest {


    @Test
    @DisplayName("Transient 어노테이션이 붙은 field 는 제거해야 한다.")
    void forQuery() {
        assertThat(
                ColumnFields.forQuery(Person.class)
                        .stream()
                        .map(Field::getName)
                        .collect(Collectors.toList())
        ).containsExactlyInAnyOrder(
                "id", "name", "age", "email"
        );
    }

    @Test
    @DisplayName("Upsert Query 에서는 Id 어노테이션이 붙은 field 를 제거한다.")
    void forUpsert() {
        assertThat(
                ColumnFields.forUpsert(Person.class)
                        .stream()
                        .map(Field::getName)
                        .collect(Collectors.toList())
        ).containsExactlyInAnyOrder(
                "name", "age", "email"
        );
    }

    @Test
    @DisplayName("식별을 위해 Id 어노테이션이 붙은 field 를 추출한다.")
    void forId() {
        assertThat(
                ColumnFields.forId(Person.class)
                        .stream()
                        .map(Field::getName)
                        .collect(Collectors.toList())
        ).containsExactlyInAnyOrder(
                "id"
        );
    }
}
