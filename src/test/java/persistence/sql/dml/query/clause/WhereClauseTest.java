package persistence.sql.dml.query.clause;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.sql.dml.conditional.Criteria;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class WhereClauseTest {

    @DisplayName("조건문을 반환한다.")
    @Test
    void whereClause() {
        Criteria criteria = Criteria.ofCriteria(Map.of("id", "123", "name", "1243"));

        assertThat(criteria.toSql()).isEqualTo("id='123'AND name='1243'");
    }

}
