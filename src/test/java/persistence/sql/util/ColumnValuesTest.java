package persistence.sql.util;

import domain.Person;
import domain.PersonFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ColumnValuesTest {

    @Test
    @DisplayName("Person 객체들의 필드값을 ColumnValues 로 추출할 수 있다.")
    void filterTransient() {
        assertThat(ColumnValues.build(
                PersonFixture.createPerson(),
                ColumnFields.forUpsert(Person.class)
        )).isEqualTo(" VALUES ('고정완', 30, 'ghojeong@email.com')");
    }
}
