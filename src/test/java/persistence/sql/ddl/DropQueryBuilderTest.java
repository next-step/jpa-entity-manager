package persistence.sql.ddl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.testfixture.notcolumn.Person;
import persistence.sql.ddl.querybuilder.DropQueryBuilder;

class DropQueryBuilderTest {
    @Test
    @DisplayName("[요구사항 4] drop table 쿼리를 만들어라")
    void 요구사항3_3_test() {
        //given
        String expectedQuery = "DROP TABLE IF EXISTS users";

        // when
        String actualQuery = new DropQueryBuilder(Person.class).getQuery();

        // then
        Assertions.assertThat(actualQuery).isEqualTo(expectedQuery);
    }
}
