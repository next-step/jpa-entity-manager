package persistence.sql.dml;

import domain.Person;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.sql.meta.Table;

@DisplayName("UpdateQueryBuilder class 의")
public class UpdateQueryBuilderTest {

    @DisplayName("generateQuery 메소드는")
    @Nested
    class GenerateQuery {

            @DisplayName("Person Entity의 update 쿼리가 만들어지는지 확인한다.")
            @Test
            void testGenerateQuery() {
                // given
                UpdateQueryBuilder updateQueryBuilder = UpdateQueryBuilder.getInstance();
                Person person = Person.of(1L, "user1", 1, "abc@test.com");

                // when
                String query = updateQueryBuilder.generateQuery(Table.getInstance(person.getClass()), person);

                // then
                assertEquals("UPDATE users SET nick_name='user1',old=1,email='abc@test.com'", query);
            }
    }
}
