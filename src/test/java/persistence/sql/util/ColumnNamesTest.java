package persistence.sql.util;

import domain.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnNamesTest {
    @Test
    @DisplayName("Person 클래스의 필드들을 ColumnNames 로 변환할 수 있다.")
    void filterTransient() {
        assertThat(ColumnNames.build(
                ColumnFields.forQuery(Person.class)
        )).isEqualTo("id, nick_name, old, email");
    }
}
