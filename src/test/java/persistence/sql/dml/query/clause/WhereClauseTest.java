package persistence.sql.dml.query.clause;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.conditional.Criteria;
import persistence.sql.dml.conditional.Criterion;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class WhereClauseTest {

    @DisplayName("조건문을 반환한다.")
    @Test
    void whereClause() {
        Criterion equalsId = Criterion.of("id", "123");
        Criterion equalsName = Criterion.of("name", "1243");
        Criteria criteria = new Criteria(List.of(equalsId, equalsName));

        assertThat(criteria.toSql()).isEqualTo("id='123'AND name='1243'");
    }

}
