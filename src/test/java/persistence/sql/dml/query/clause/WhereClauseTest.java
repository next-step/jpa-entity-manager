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
        Criteria criteria = Criteria.ofCriteria(List.of(
                Criterion.of("id", "123"),
                Criterion.of("name", "1243")
        ));

        assertThat(criteria.toSql()).isEqualTo("id='123'AND name='1243'");
    }

}
