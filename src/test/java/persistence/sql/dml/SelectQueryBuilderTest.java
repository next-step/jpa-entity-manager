package persistence.sql.dml;

import domain.Person;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import persistence.sql.meta.Table;

@DisplayName("SelectQueryBuilder class 의")
class SelectQueryBuilderTest {

    @DisplayName("generateQuery 메소드는")
    @Nested
    class GenerateQuery {

        @DisplayName("Person Entity의 select 쿼리가 만들어지는지 확인한다.")
        @Test
        void testGenerateQuery() {
            // given
            SelectQueryBuilder selectQueryBuilder = SelectQueryBuilder.getInstance();

            // when
            String query = selectQueryBuilder.generateQuery(Table.getInstance(Person.class));

            // then
            assertEquals("SELECT id,nick_name,old,email FROM users", query);
        }
    }
}
