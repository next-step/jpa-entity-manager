package persistence.sql.dml;

import domain.Person;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.sql.meta.Table;

@DisplayName("DeleteQueryBuilder class 의")
class DeleteQueryBuilderTest {

    @DisplayName("generateQuery 메서드는")
    @Nested
    class GenerateQuery {

        @DisplayName("Person Entity의 delete 쿼리가 만들어지는지 확인한다.")
        @Test
        void testGenerateQuery() {
            // given
            DeleteQueryBuilder deleteQueryBuilder = DeleteQueryBuilder.getInstance();

            // when
            String query = deleteQueryBuilder.generateQuery(Table.getInstance(Person.class));

            // then
            assertEquals("DELETE FROM users", query);
        }
    }
}
