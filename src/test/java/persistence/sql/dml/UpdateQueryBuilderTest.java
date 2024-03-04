package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.entity.Person;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateQueryBuilderTest {

    @Test
    @DisplayName("Person 객체를 이용한 DML UPDATE 생성 테스트")
    void DMLUpdateTest() {
        // given
        String expectedQuery = "UPDATE users SET nick_name = 'Jamie', old = 34 WHERE id = 1;";
        Person person = new Person(1L, "Jamie", 34, null);

        // when
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(person);

        // then
        assertThat(updateQueryBuilder.build()).isEqualTo(expectedQuery);
    }

}