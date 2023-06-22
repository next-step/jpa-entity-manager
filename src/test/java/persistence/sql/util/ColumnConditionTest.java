package persistence.sql.util;

import domain.Person;
import domain.PersonFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnConditionTest {

    @Test
    @DisplayName("Entity 의 필드를 조건문으로 바꿀 수 있다.")
    void build() {
        final Person person = PersonFixture.createPerson();
        final List<String> actual = ColumnFields.forUpsert(Person.class)
                .stream().map(
                        field -> ColumnCondition.build(field, person)
                ).collect(Collectors.toList());
        assertThat(actual).containsExactlyInAnyOrder(
                "nick_name = '고정완'",
                "old = 30",
                "email = 'ghojeong@email.com'"
        );
    }
}
