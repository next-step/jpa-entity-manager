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
        String expectedQuery = "UPDATE users SET nick_name = 'Jamie', old = 34, email = 'jamie@mail.com' WHERE id = 1;";
        Person person = new Person(1L, "Jamie", 34, "jamie@mail.com");

        // when
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(person);

        // then
        assertThat(updateQueryBuilder.build()).isEqualTo(expectedQuery);
    }

    @Test
    @DisplayName("null을 가진 상태 값을 포함한 Person 객체를 이용한 DML UPDATE 생성 테스트")
    void DMLUpdate2Test() {
        // given
        String expectedQuery = "UPDATE users SET nick_name = null, old = 34, email = 'jamie@mail.com' WHERE id = 1;";
        Person person = new Person(1L, null, 34, "jamie@mail.com");

        // when
        UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder(person);

        // then
        assertThat(updateQueryBuilder.build()).isEqualTo(expectedQuery);
    }

}
