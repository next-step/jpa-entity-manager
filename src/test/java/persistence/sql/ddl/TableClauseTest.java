package persistence.sql.ddl;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.entity.testfixture.annotated.Person;
import persistence.sql.ddl.clause.table.TableClause;
import persistence.sql.ddl.querybuilder.CreateQueryBuilder;

class TableClauseTest {
    @Test
    @DisplayName("[요구사항 2] @ColumnClause 애노테이션이 있는 Person 엔티티를 이용하여 create 쿼리 만든다.")
    void 요구사항2_test() {
        //given
        String expectedQuery = "CREATE TABLE IF NOT EXISTS Person " +
                "(id Long AUTO_INCREMENT PRIMARY KEY,nick_name VARCHAR(30) NULL,old INT NULL,email VARCHAR(30) NOT NULL)";

        // when
        String actualQuery = new CreateQueryBuilder(Person.class).getQuery();

        // then
        Assertions.assertThat(actualQuery).isEqualTo(expectedQuery);
    }

    @Test
    @DisplayName("[요구사항 3] 3.1 @Table 애노테이션이 붙은 필드의 name을 테이블명으로 가지는 create 쿼리 만든다.")
    void 요구사항3_1_test() {
        //given
        String expectedName = "users";

        // when
        String actualName = new TableClause(persistence.entity.testfixture.notcolumn.Person.class).name();

        // then
        Assertions.assertThat(actualName).isEqualTo(expectedName);
    }
}
